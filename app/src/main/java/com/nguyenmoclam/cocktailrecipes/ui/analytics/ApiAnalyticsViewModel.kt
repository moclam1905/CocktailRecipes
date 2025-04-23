package com.nguyenmoclam.cocktailrecipes.ui.analytics

import androidx.lifecycle.viewModelScope
import com.nguyenmoclam.cocktailrecipes.data.remote.analytics.ApiAnalyticsManager
import com.nguyenmoclam.cocktailrecipes.data.remote.interceptor.PerformanceTrackingInterceptor
import com.nguyenmoclam.cocktailrecipes.ui.analytics.ApiAnalyticsViewModel.ErrorDetails
import com.nguyenmoclam.cocktailrecipes.ui.analytics.ApiAnalyticsViewModel.SlowCallDetails
import com.nguyenmoclam.cocktailrecipes.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

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
 * Events for the API analytics dashboard
 */
sealed class ApiAnalyticsEvent {
    object RefreshData : ApiAnalyticsEvent()
    object GenerateReport : ApiAnalyticsEvent()
    object ResetAnalytics : ApiAnalyticsEvent()
}

/**
 * ViewModel for the API analytics dashboard
 */
@HiltViewModel
class ApiAnalyticsViewModel @Inject constructor(
    private val analyticsManager: ApiAnalyticsManager,
    private val performanceInterceptor: PerformanceTrackingInterceptor
) : BaseViewModel<ApiAnalyticsUiState, ApiAnalyticsEvent>(ApiAnalyticsUiState()) {

    init {
        // Load initial data
        handleEvent(ApiAnalyticsEvent.RefreshData)

        // Observe error rates
        viewModelScope.launch {
            analyticsManager.getErrorRates().collect { errorRates ->
                // Update UI state with error rates if needed
            }
        }
    }

    override suspend fun processEvent(event: ApiAnalyticsEvent) {
        when (event) {
            is ApiAnalyticsEvent.RefreshData -> refreshData()
            is ApiAnalyticsEvent.GenerateReport -> generateReport()
            is ApiAnalyticsEvent.ResetAnalytics -> resetAnalytics()
        }
    }

    /**
     * Refresh analytics data
     */
    private suspend fun refreshData() {
        // Get current metrics from the performance interceptor
        val metrics = performanceInterceptor.getPerformanceMetrics()

        // Convert errors and slow calls to display format
        val errorEvents = mutableListOf<ErrorDetails>()
        val slowCallEvents = mutableListOf<SlowCallDetails>()

        // In a real app, you would fetch these from the analytics manager
        // For now, we'll just use sample data until we integrate with the manager

        // Update UI state
        updateState { currentState ->
            currentState.copy(
                endpointMetrics = metrics,
                recentErrors = errorEvents,
                recentSlowCalls = slowCallEvents,
                lastUpdated = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
            )
        }
    }

    /**
     * Generate a performance report
     */
    private suspend fun generateReport() {
        val reportPath = analyticsManager.generatePerformanceReport()
        updateState { it.copy(lastReportPath = reportPath) }
    }

    /**
     * Reset analytics data
     */
    private suspend fun resetAnalytics() {
        analyticsManager.resetAnalytics()
        refreshData()
    }

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