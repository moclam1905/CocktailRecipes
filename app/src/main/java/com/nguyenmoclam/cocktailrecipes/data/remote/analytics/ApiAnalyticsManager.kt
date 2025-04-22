package com.nguyenmoclam.cocktailrecipes.data.remote.analytics

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nguyenmoclam.cocktailrecipes.data.remote.interceptor.PerformanceTrackingInterceptor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Manager for API analytics tracking and reporting
 */
@Singleton
class ApiAnalyticsManager @Inject constructor(
    private val performanceInterceptorProvider: Provider<PerformanceTrackingInterceptor>,
    @ApplicationContext private val context: Context
) {
    private val TAG = "ApiAnalyticsManager"
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    
    // Track errors for reporting
    private val recentErrors = ConcurrentHashMap<String, MutableList<ApiErrorEvent>>()
    private val recentSlowCalls = ConcurrentHashMap<String, MutableList<ApiSlowCallEvent>>()
    
    // Constants
    private val VERY_SLOW_THRESHOLD_MS = 3000L
    private val ERROR_HISTORY_SIZE = 50
    private val SLOW_CALL_HISTORY_SIZE = 50
    
    // DataStore for persisting analytics
    private val Context.analyticsDataStore by preferencesDataStore(name = "api_analytics")
    private val LAST_REPORT_TIME_KEY = longPreferencesKey("last_report_time")
    private val ERROR_RATE_KEY = stringPreferencesKey("error_rates")
    
    init {
        // Start periodic reporting
        coroutineScope.launch {
            startPeriodicReporting()
        }
    }
    
    /**
     * Record an API error for reporting
     */
    fun recordApiError(endpoint: String, statusCode: Int, errorMessage: String) {
        val errors = recentErrors.computeIfAbsent(endpoint) { mutableListOf() }
        synchronized(errors) {
            errors.add(ApiErrorEvent(endpoint, statusCode, errorMessage, System.currentTimeMillis()))
            // Keep list size in check
            if (errors.size > ERROR_HISTORY_SIZE) {
                errors.removeAt(0)
            }
        }

        Timber.tag(TAG).e("API Error: $endpoint returned $statusCode - $errorMessage")
    }
    
    /**
     * Record a slow API call for reporting
     */
    fun recordSlowApiCall(endpoint: String, durationMs: Long) {
        // Only track very slow calls
        if (durationMs > VERY_SLOW_THRESHOLD_MS) {
            val slowCalls = recentSlowCalls.computeIfAbsent(endpoint) { mutableListOf() }
            synchronized(slowCalls) {
                slowCalls.add(ApiSlowCallEvent(endpoint, durationMs, System.currentTimeMillis()))
                // Keep list size in check
                if (slowCalls.size > SLOW_CALL_HISTORY_SIZE) {
                    slowCalls.removeAt(0)
                }
            }

            Timber.tag(TAG).w("Very slow API call: $endpoint took $durationMs ms")
        }
    }
    
    /**
     * Get a flow of the error rate data for monitoring
     */
    fun getErrorRates(): Flow<Map<String, Double>> {
        return context.analyticsDataStore.data.map { preferences ->
            val errorRatesJson = preferences[ERROR_RATE_KEY] ?: "{}"
            parseErrorRatesJson(errorRatesJson)
        }
    }
    
    /**
     * Create a performance report
     */
    fun generatePerformanceReport(): String {
        val reportJson = JSONObject()
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        
        // Add report metadata
        reportJson.put("timestamp", timestamp)
        reportJson.put("app_version", getAppVersion())
        
        // Get the performanceInterceptor instance lazily
        val performanceInterceptor = performanceInterceptorProvider.get()
        
        // Add performance metrics from interceptor
        val performanceMetrics = performanceInterceptor.getPerformanceMetrics()
        val endpointsArray = JSONArray()
        
        performanceMetrics.forEach { (endpoint, metrics) ->
            val endpointJson = JSONObject()
            endpointJson.put("endpoint", endpoint)
            endpointJson.put("avg_duration_ms", metrics.avgDurationMs)
            endpointJson.put("max_duration_ms", metrics.maxDurationMs)
            endpointJson.put("total_calls", metrics.totalCalls)
            endpointJson.put("error_count", metrics.errorCount)
            
            val errorRate = if (metrics.totalCalls > 0) {
                (metrics.errorCount.toDouble() / metrics.totalCalls) * 100
            } else {
                0.0
            }
            endpointJson.put("error_rate", errorRate)
            
            endpointsArray.put(endpointJson)
        }
        
        reportJson.put("endpoints", endpointsArray)
        
        // Add recent errors
        val errorsArray = JSONArray()
        recentErrors.forEach { (_, errors) ->
            synchronized(errors) {
                errors.forEach { error ->
                    val errorJson = JSONObject()
                    errorJson.put("endpoint", error.endpoint)
                    errorJson.put("status_code", error.statusCode)
                    errorJson.put("message", error.message)
                    errorJson.put("timestamp", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(error.timestamp)))
                    errorsArray.put(errorJson)
                }
            }
        }
        
        reportJson.put("recent_errors", errorsArray)
        
        // Add slow calls
        val slowCallsArray = JSONArray()
        recentSlowCalls.forEach { (_, slowCalls) ->
            synchronized(slowCalls) {
                slowCalls.forEach { slowCall ->
                    val slowCallJson = JSONObject()
                    slowCallJson.put("endpoint", slowCall.endpoint)
                    slowCallJson.put("duration_ms", slowCall.durationMs)
                    slowCallJson.put("timestamp", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(slowCall.timestamp)))
                    slowCallsArray.put(slowCallJson)
                }
            }
        }
        
        reportJson.put("slow_calls", slowCallsArray)
        
        // Write report to file in app's files directory
        val reportFile = File(context.filesDir, "api_performance_${System.currentTimeMillis()}.json")
        reportFile.writeText(reportJson.toString(2))
        
        // Update last report time
        coroutineScope.launch {
            context.analyticsDataStore.edit { preferences ->
                preferences[LAST_REPORT_TIME_KEY] = System.currentTimeMillis()
                
                // Also update error rates
                val errorRatesJson = JSONObject()
                performanceMetrics.forEach { (endpoint, metrics) ->
                    val errorRate = if (metrics.totalCalls > 0) {
                        (metrics.errorCount.toDouble() / metrics.totalCalls) * 100
                    } else {
                        0.0
                    }
                    errorRatesJson.put(endpoint, errorRate)
                }
                preferences[ERROR_RATE_KEY] = errorRatesJson.toString()
            }
        }

        Timber.tag(TAG).i("Generated performance report at ${reportFile.absolutePath}")
        return reportFile.absolutePath
    }
    
    /**
     * Reset all analytics data
     */
    fun resetAnalytics() {
        // Use the provider to get the instance only when needed
        performanceInterceptorProvider.get().resetMetrics()
        recentErrors.clear()
        recentSlowCalls.clear()
    }
    
    private fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "${packageInfo.versionName} (${packageInfo.versionCode})"
        } catch (e: Exception) {
            "Unknown"
        }
    }
    
    private suspend fun startPeriodicReporting() {
        // Daily automatic reporting
        try {
            // Get the last report time from DataStore
            context.analyticsDataStore.data.first().let { preferences ->
                val lastReportTime = preferences[LAST_REPORT_TIME_KEY] ?: 0L
                val currentTime = System.currentTimeMillis()
                
                // Check if 24 hours have passed since the last report
                val oneDayInMillis = 24 * 60 * 60 * 1000L
                
                // If this is the first report or more than a day has passed, generate a report
                if (lastReportTime == 0L || currentTime - lastReportTime > oneDayInMillis) {
                    // Generate a report if there are metrics to report
                    if (performanceInterceptorProvider.get().getPerformanceMetrics().isNotEmpty()) {
                        Timber.tag(TAG).i("Starting periodic report generation")
                        generatePerformanceReport()
                    }
                }
            }
            
            // Schedule the next check in 1 hour
            delay(60 * 60 * 1000L) // 1 hour
            startPeriodicReporting() // Recursive call for continuous checking
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error in periodic reporting")
            
            // If an error occurs, retry after 4 hours
            delay(4 * 60 * 60 * 1000L)
            startPeriodicReporting()
        }
    }
    
    private fun parseErrorRatesJson(json: String): Map<String, Double> {
        return try {
            val result = mutableMapOf<String, Double>()
            val jsonObject = JSONObject(json)
            
            jsonObject.keys().forEach { key ->
                result[key] = jsonObject.optDouble(key, 0.0)
            }
            
            result
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error parsing error rates JSON")
            emptyMap()
        }
    }
    
    /**
     * Data class for API error events
     */
    data class ApiErrorEvent(
        val endpoint: String,
        val statusCode: Int,
        val message: String,
        val timestamp: Long
    )
    
    /**
     * Data class for slow API call events
     */
    data class ApiSlowCallEvent(
        val endpoint: String,
        val durationMs: Long,
        val timestamp: Long
    )
} 