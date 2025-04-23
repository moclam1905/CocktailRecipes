package com.nguyenmoclam.cocktailrecipes.ui.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.nguyenmoclam.cocktailrecipes.ui.base.BaseViewModel
import com.nguyenmoclam.cocktailrecipes.ui.common.showRateLimitErrorToast

/**
 * A composable that observes and handles rate limit errors from a BaseViewModel
 * Displays either a Snackbar or Toast depending on the availability of SnackbarHostState
 */
@Composable
fun <S, E> RateLimitErrorObserver(
    viewModel: BaseViewModel<S, E>,
    snackbarHostState: SnackbarHostState? = null,
    showToast: Boolean = true
) {
    val context = LocalContext.current
    val rateLimitError by viewModel.rateLimitError.collectAsState()
    
    LaunchedEffect(rateLimitError) {
        rateLimitError?.let { error ->
            if (snackbarHostState != null) {
                // Show Snackbar if SnackbarHostState is provided
                snackbarHostState.showSnackbar(
                    message = "Rate limit reached. Please try again later.",
                    duration = SnackbarDuration.Long
                )
            } else if (showToast) {
                // Fall back to Toast if no SnackbarHostState
                context.showRateLimitErrorToast(error.message ?: "Unknown rate limit error")
            }
            
            // Clear the error after showing
            viewModel.clearRateLimitError()
        }
    }
} 