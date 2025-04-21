package com.nguyenmoclam.cocktailrecipes.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nguyenmoclam.cocktailrecipes.R
import com.nguyenmoclam.cocktailrecipes.ui.theme.CocktailRecipesTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Enhanced error view with animated transition and contextual error display
 */
@Composable
fun EnhancedErrorView(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit,
    errorType: ErrorType = ErrorType.GENERAL,
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val errorIcon = when (errorType) {
                ErrorType.NETWORK -> Icons.Filled.CloudOff
                ErrorType.SERVER -> Icons.Filled.Warning
                ErrorType.TIMEOUT -> Icons.Filled.NetworkCheck
                else -> Icons.Filled.ErrorOutline
            }
            
            // Error icon with background
            Surface(
                shape = RoundedCornerShape(percent = 50),
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = errorIcon,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Error title
            Text(
                text = errorTypeToTitle(errorType),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Error message
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Retry button
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(48.dp)
            ) {
                Text("Try Again")
            }
        }
    }
}

/**
 * Banner-style error notification for non-critical errors
 */
@Composable
fun ErrorBanner(
    modifier: Modifier = Modifier,
    message: String,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null,
    duration: Long = 5000 // Auto-dismiss after 5 seconds
) {
    val coroutineScope = rememberCoroutineScope()
    var isVisible by remember { mutableStateOf(true) }
    
    // Auto dismiss after duration
    LaunchedEffect(Unit) {
        if (duration > 0) {
            delay(duration)
            isVisible = false
            onDismiss()
        }
    }
    
    if (isVisible) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = MaterialTheme.colorScheme.errorContainer,
            shape = RoundedCornerShape(8.dp),
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.ErrorOutline,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.weight(1f)
                )
                
                if (onRetry != null) {
                    TextButton(
                        onClick = {
                            onRetry()
                            isVisible = false
                            onDismiss()
                        }
                    ) {
                        Text(
                            text = "Retry",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                IconButton(
                    onClick = {
                        isVisible = false
                        onDismiss()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "Dismiss",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

/**
 * Interactive network error view with offline mode option
 */
@Composable
fun NetworkErrorView(
    onRetry: () -> Unit,
    onOfflineMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.placeholder_cocktail),
            contentDescription = "Network Error",
            modifier = Modifier.size(120.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "No Internet Connection",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Please check your connection and try again or use the app in offline mode.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Column(
            modifier = Modifier.fillMaxWidth(0.7f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.NetworkCheck,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry Connection")
            }
            
            OutlinedButton(
                onClick = onOfflineMode,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue Offline")
            }
        }
    }
}

/**
 * Edge case error handler for specific application errors
 */
@Composable
fun EdgeCaseErrorHandler(
    message: String,
    errorType: EdgeCaseType,
    onAction: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (actionText, icon) = when (errorType) {
        EdgeCaseType.EMPTY_RESULTS -> "Try Different Search" to Icons.Filled.ErrorOutline
        EdgeCaseType.RATE_LIMIT -> "Try Later" to Icons.Filled.Warning
        EdgeCaseType.PERMISSION_DENIED -> "Open Settings" to Icons.Filled.Warning
        EdgeCaseType.DATA_INCONSISTENCY -> "Refresh Data" to Icons.Filled.ErrorOutline
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = edgeCaseTypeToTitle(errorType),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Dismiss")
                }
                
                Button(
                    onClick = onAction,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(actionText)
                }
            }
        }
    }
}

/**
 * Error types for contextual error handling
 */
enum class ErrorType {
    GENERAL,
    NETWORK,
    SERVER,
    TIMEOUT
}

/**
 * Edge case error types for specific application errors
 */
enum class EdgeCaseType {
    EMPTY_RESULTS,
    RATE_LIMIT,
    PERMISSION_DENIED,
    DATA_INCONSISTENCY
}

/**
 * Map error type to title
 */
private fun errorTypeToTitle(errorType: ErrorType): String = when (errorType) {
    ErrorType.NETWORK -> "Network Error"
    ErrorType.SERVER -> "Server Error"
    ErrorType.TIMEOUT -> "Request Timed Out"
    else -> "Oops! Something Went Wrong"
}

/**
 * Map edge case type to title
 */
private fun edgeCaseTypeToTitle(edgeCaseType: EdgeCaseType): String = when (edgeCaseType) {
    EdgeCaseType.EMPTY_RESULTS -> "No Results Found"
    EdgeCaseType.RATE_LIMIT -> "Rate Limit Exceeded"
    EdgeCaseType.PERMISSION_DENIED -> "Permission Required"
    EdgeCaseType.DATA_INCONSISTENCY -> "Data Error"
}

// Previews
@Preview(showBackground = true)
@Composable
fun EnhancedErrorViewPreview() {
    CocktailRecipesTheme {
        EnhancedErrorView(
            message = "Failed to load cocktails. Please check your connection and try again.",
            onRetry = {},
            errorType = ErrorType.NETWORK
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorBannerPreview() {
    CocktailRecipesTheme {
        ErrorBanner(
            message = "Failed to update favorite status.",
            onDismiss = {},
            onRetry = {},
            duration = 0 // Disable auto-dismiss for preview
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NetworkErrorViewPreview() {
    CocktailRecipesTheme {
        NetworkErrorView(
            onRetry = {},
            onOfflineMode = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EdgeCaseErrorHandlerPreview() {
    CocktailRecipesTheme {
        EdgeCaseErrorHandler(
            message = "You've reached the API rate limit. Please try again later.",
            errorType = EdgeCaseType.RATE_LIMIT,
            onAction = {},
            onDismiss = {}
        )
    }
} 