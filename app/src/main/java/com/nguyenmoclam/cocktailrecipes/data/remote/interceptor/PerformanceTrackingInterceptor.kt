package com.nguyenmoclam.cocktailrecipes.data.remote.interceptor

import android.util.Log
import com.nguyenmoclam.cocktailrecipes.data.remote.analytics.ApiAnalyticsManager
import dagger.Lazy
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor that tracks and logs API call performance metrics
 * - Measures call durations
 * - Tracks error rates
 * - Identifies slow endpoints
 */
@Singleton
class PerformanceTrackingInterceptor @Inject constructor(
    private val analyticsManager: Lazy<ApiAnalyticsManager>
) : Interceptor {
    
    private val TAG = "PerformanceTracking"
    
    // Track metrics for each endpoint
    private val endpointMetrics = ConcurrentHashMap<String, EndpointMetrics>()
    
    // Thresholds for performance warnings
    private val SLOW_CALL_THRESHOLD_MS = 1500L // 1.5 seconds
    private val VERY_SLOW_CALL_THRESHOLD_MS = 3000L // 3 seconds
    
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val endpoint = request.url.encodedPath
        
        // Start timing
        val startTime = System.nanoTime()
        var response: Response? = null
        var exception: IOException? = null
        
        try {
            // Execute the request
            response = chain.proceed(request)
            return response
        } catch (e: IOException) {
            exception = e
            // Record API error
            analyticsManager.get().recordApiError(
                endpoint,
                -1, // No status code available for IOException
                e.message ?: "Network error"
            )
            throw e
        } finally {
            // Calculate duration
            val endTime = System.nanoTime()
            val durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime)
            
            // Get or create metrics for this endpoint
            val metrics = endpointMetrics.computeIfAbsent(endpoint) { EndpointMetrics() }
            
            // Update metrics
            synchronized(metrics) {
                metrics.totalCalls++
                metrics.totalDurationMs += durationMs
                
                if (exception != null || (response != null && !response.isSuccessful)) {
                    metrics.errorCount++
                    
                    // Record API error if response is not successful
                    if (exception == null && response != null) {
                        analyticsManager.get().recordApiError(
                            endpoint,
                            response.code,
                            "HTTP Error: ${response.code}"
                        )
                    }
                }
                
                if (durationMs > metrics.maxDurationMs) {
                    metrics.maxDurationMs = durationMs
                }
                
                // Update moving average (simple exponential moving average)
                val alpha = 0.2 // Smoothing factor
                if (metrics.avgDurationMs == 0L) {
                    metrics.avgDurationMs = durationMs
                } else {
                    metrics.avgDurationMs = (alpha * durationMs + (1 - alpha) * metrics.avgDurationMs).toLong()
                }
            }
            
            // Record slow API call if applicable
            if (durationMs > VERY_SLOW_CALL_THRESHOLD_MS) {
                analyticsManager.get().recordSlowApiCall(endpoint, durationMs)
            }
            
            // Log performance data
            logPerformance(endpoint, durationMs, response, exception, metrics)
        }
    }
    
    private fun logPerformance(endpoint: String, durationMs: Long, response: Response?, exception: IOException?, metrics: EndpointMetrics) {
        val status = when {
            exception != null -> "ERROR: ${exception.javaClass.simpleName}"
            response == null -> "UNKNOWN"
            response.isSuccessful -> "SUCCESS: ${response.code}"
            else -> "FAILURE: ${response.code}"
        }
        
        // Determine log level based on performance
        when {
            durationMs > VERY_SLOW_CALL_THRESHOLD_MS -> {
                Timber.tag(TAG).w("VERY SLOW API CALL: $endpoint completed in $durationMs ms with status $status")
            }
            durationMs > SLOW_CALL_THRESHOLD_MS -> {
                Timber.tag(TAG).i("SLOW API CALL: $endpoint completed in $durationMs ms with status $status")
            }
            else -> {
                Timber.tag(TAG).d("API CALL: $endpoint completed in $durationMs ms with status $status")
            }
        }
        
        // Periodically log endpoint statistics (every 20 calls)
        synchronized(metrics) {
            if ((metrics.totalCalls % 20).toInt() == 0) {
                val errorRate = if (metrics.totalCalls > 0) {
                    (metrics.errorCount * 100 / metrics.totalCalls.toDouble())
                } else {
                    0.0
                }
                
                Timber.tag(TAG).i("ENDPOINT STATS: $endpoint - " +
                        "Avg: ${metrics.avgDurationMs}ms, " +
                        "Max: ${metrics.maxDurationMs}ms, " +
                        "Calls: ${metrics.totalCalls}, " +
                        "Error rate: ${String.format("%.1f", errorRate)}%")
            }
        }
    }
    
    /**
     * Get current performance metrics snapshot - can be used for reporting/dashboard
     */
    fun getPerformanceMetrics(): Map<String, EndpointMetrics> {
        return endpointMetrics.toMap()
    }
    
    /**
     * Reset all collected metrics
     */
    fun resetMetrics() {
        endpointMetrics.clear()
    }
    
    /**
     * Data class that holds performance metrics for an endpoint
     */
    data class EndpointMetrics(
        var totalCalls: Long = 0,
        var errorCount: Long = 0,
        var totalDurationMs: Long = 0,
        var avgDurationMs: Long = 0,
        var maxDurationMs: Long = 0
    )
} 