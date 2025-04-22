package com.nguyenmoclam.cocktailrecipes.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nguyenmoclam.cocktailrecipes.data.remote.analytics.ApiAnalyticsManager
import com.nguyenmoclam.cocktailrecipes.data.remote.interceptor.PerformanceTrackingInterceptor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel for the API analytics dashboard
 */
@HiltViewModel
class ApiAnalyticsViewModel @Inject constructor(
    private val analyticsManager: ApiAnalyticsManager,
    private val performanceInterceptor: PerformanceTrackingInterceptor
) : ViewModel() {

    // UI state for the dashboard
    private val _uiState = MutableStateFlow(ApiAnalyticsUiState())
    val uiState: StateFlow<ApiAnalyticsUiState> = _uiState.asStateFlow()

    init {
        // Load initial data
        refreshData()

        // Observe error rates
        viewModelScope.launch {
            analyticsManager.getErrorRates().collect { errorRates ->
                // Update UI state with error rates if needed
            }
        }
    }

    /**
     * Refresh analytics data
     */
    fun refreshData() {
        viewModelScope.launch {
            // Get current metrics from the performance interceptor
            val metrics = performanceInterceptor.getPerformanceMetrics()

            // Convert errors and slow calls to display format
            val errorEvents = mutableListOf<ErrorDetails>()
            val slowCallEvents = mutableListOf<SlowCallDetails>()

            // In a real app, you would fetch these from the analytics manager
            // For now, we'll just use sample data until we integrate with the manager

            // Update UI state
            _uiState.update { currentState ->
                currentState.copy(
                    endpointMetrics = metrics,
                    recentErrors = errorEvents,
                    recentSlowCalls = slowCallEvents,
                    lastUpdated = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
                )
            }
        }
    }

    /**
     * Generate a performance report
     */
    fun generateReport() {
        viewModelScope.launch {
            val reportPath = analyticsManager.generatePerformanceReport()
            _uiState.update { it.copy(lastReportPath = reportPath) }
        }
    }

    /**
     * Reset analytics data
     */
    fun resetAnalytics() {
        viewModelScope.launch {
            analyticsManager.resetAnalytics()
            refreshData()
        }
    }

    /**
     * UI state for the API analytics dashboard
     */
    data class ApiAnalyticsUiState(
        val endpointMetrics: Map<String, PerformanceTrackingInterceptor.EndpointMetrics> = emptyMap(),
        val recentErrors: List<ErrorDetails> = emptyList(),
        val recentSlowCalls: List<SlowCallDetails> = emptyList(),
        val lastUpdated: String = "",
        val lastReportPath: String = ""
    )

    /**
     * Error details for display
     */
    data class ErrorDetails(
        val endpoint: String,
        val statusCode: Int,
        val message: String,
        val timestamp: String
    )

    /**
     * Slow call details for display
     */
    data class SlowCallDetails(
        val endpoint: String,
        val durationMs: Long,
        val timestamp: String
    )
}