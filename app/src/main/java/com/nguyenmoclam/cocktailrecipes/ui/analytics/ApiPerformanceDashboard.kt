package com.nguyenmoclam.cocktailrecipes.ui.analytics

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nguyenmoclam.cocktailrecipes.R
import com.nguyenmoclam.cocktailrecipes.data.remote.interceptor.PerformanceTrackingInterceptor
import com.nguyenmoclam.cocktailrecipes.ui.analytics.ApiAnalyticsViewModel.ErrorDetails
import com.nguyenmoclam.cocktailrecipes.ui.analytics.ApiAnalyticsViewModel.SlowCallDetails
import com.nguyenmoclam.cocktailrecipes.ui.theme.CocktailRecipesTheme
import kotlinx.coroutines.launch

/**
 * Dashboard screen for visualizing API performance analytics
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiPerformanceDashboard(
    viewModel: ApiAnalyticsViewModel = hiltViewModel(),
    onGenerateReport: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(DashboardTab.PERFORMANCE) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "API Performance Dashboard",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { 
            SnackbarHost(hostState = snackbarHostState) 
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Tab selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DashboardTab.entries.forEach { tab ->
                    TabButton(
                        text = tab.title,
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab }
                    )
                }
            }
            
            // Refresh button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    scope.launch {
                        viewModel.handleEvent(ApiAnalyticsEvent.RefreshData)
                        snackbarHostState.showSnackbar("Dashboard data refreshed")
                    }
                }) {
                    Text("Refresh Data")
                }
            }
            
            // Dashboard summary
            SummaryCard(
                totalCalls = uiState.endpointMetrics.values.sumOf { it.totalCalls },
                avgResponseTime = uiState.endpointMetrics.values.map { it.avgDurationMs }.average().toLong(),
                errorRate = calculateOverallErrorRate(uiState.endpointMetrics.values.toList())
            )
            
            // Dashboard content based on selected tab
            when (selectedTab) {
                DashboardTab.PERFORMANCE -> PerformanceTab(uiState.endpointMetrics)
                DashboardTab.ERRORS -> ErrorsTab(uiState.recentErrors)
                DashboardTab.SLOW_CALLS -> SlowCallsTab(uiState.recentSlowCalls)
            }
            
            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    scope.launch {
                        viewModel.handleEvent(ApiAnalyticsEvent.GenerateReport)
                        onGenerateReport()
                        // Show a toast or snackbar to inform the user
                        snackbarHostState.showSnackbar(
                            message = "Report generated at: ${uiState.lastReportPath}"
                        )
                    }
                }) {
                    Text("Generate Report")
                }
                
                Button(onClick = {
                    scope.launch {
                        viewModel.handleEvent(ApiAnalyticsEvent.ResetAnalytics)
                        snackbarHostState.showSnackbar(
                            message = "Analytics data has been reset"
                        )
                    }
                }) {
                    Text("Reset Analytics")
                }
            }
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = text,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
            
            if (selected) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(2.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(1.dp))
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun SummaryCard(
    totalCalls: Long,
    avgResponseTime: Long,
    errorRate: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Overall Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    label = "Total Calls",
                    value = totalCalls.toString()
                )
                
                SummaryItem(
                    label = "Avg Response",
                    value = "$avgResponseTime ms"
                )
                
                SummaryItem(
                    label = "Error Rate",
                    value = String.format("%.1f%%", errorRate),
                    valueColor = if (errorRate > 5.0) Color.Red else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
private fun PerformanceTab(
    endpointMetrics: Map<String, PerformanceTrackingInterceptor.EndpointMetrics>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Table header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp)
        ) {
            Text(
                text = "Endpoint",
                modifier = Modifier.weight(3f),
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Avg",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Max",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Calls",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Errors",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Table rows
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            items(endpointMetrics.entries.toList()) { (endpoint, metrics) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                ) {
                    Text(
                        text = endpoint,
                        modifier = Modifier.weight(3f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = "${metrics.avgDurationMs} ms",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = getResponseTimeColor(metrics.avgDurationMs)
                    )
                    
                    Text(
                        text = "${metrics.maxDurationMs} ms",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = "${metrics.totalCalls}",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = "${metrics.errorCount}",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (metrics.errorCount > 0) Color.Red else MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Divider()
            }
        }
    }
}

@Composable
private fun ErrorsTab(
    errors: List<ErrorDetails>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (errors.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No errors recorded",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                items(errors) { error ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = error.endpoint,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Text(
                                    text = "Status: ${error.statusCode}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = error.message,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = error.timestamp,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SlowCallsTab(
    slowCalls: List<SlowCallDetails>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (slowCalls.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No slow API calls recorded",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                items(slowCalls) { slowCall ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = slowCall.endpoint,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Text(
                                    text = "${slowCall.durationMs} ms",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = getResponseTimeColor(slowCall.durationMs)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = slowCall.timestamp,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Helper function to calculate the overall error rate
 */
private fun calculateOverallErrorRate(metrics: List<PerformanceTrackingInterceptor.EndpointMetrics>): Double {
    val totalCalls = metrics.sumOf { it.totalCalls }
    val totalErrors = metrics.sumOf { it.errorCount }
    
    return if (totalCalls > 0) {
        (totalErrors.toDouble() / totalCalls) * 100
    } else {
        0.0
    }
}

/**
 * Helper function to determine color based on response time
 */
private fun getResponseTimeColor(timeMs: Long): Color {
    return when {
        timeMs > 3000 -> Color.Red
        timeMs > 1500 -> Color(0xFFFFA000) // Orange
        timeMs > 500 -> Color(0xFF00897B) // Teal
        else -> Color(0xFF4CAF50) // Green
    }
}

/**
 * Enum for dashboard tabs
 */
private enum class DashboardTab(val title: String) {
    PERFORMANCE("Performance"),
    ERRORS("Errors"),
    SLOW_CALLS("Slow Calls")
}

@Preview(showBackground = true)
@Composable
private fun ApiPerformanceDashboardPreview() {
    CocktailRecipesTheme {
        ApiPerformanceDashboard()
    }
} 