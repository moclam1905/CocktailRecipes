package com.nguyenmoclam.cocktailrecipes.ui.common

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.nguyenmoclam.cocktailrecipes.data.common.ApiCallException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Extension function to show a toast message for API rate limit errors
 */
fun Context.showRateLimitErrorToast(message: String) {
    Toast.makeText(
        this,
        "Rate limit reached: $message. Please try again later.",
        Toast.LENGTH_LONG
    ).show()
}

/**
 * Extension function to check if an exception is related to rate limiting
 */
fun Throwable.isRateLimitError(): Boolean {
    return this is ApiCallException && 
           message?.contains("rate limit", ignoreCase = true) == true
}

/**
 * Composable function to show a snackbar for rate limit errors
 */
@Composable
fun RateLimitErrorHandler(
    error: Throwable?,
    snackbarHostState: SnackbarHostState,
    onErrorShown: () -> Unit
) {
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(error) {
        error?.let {
            if (it.isRateLimitError()) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Rate limit reached. Please try again later.",
                    )
                    onErrorShown()
                }
            }
        }
    }
}

/**
 * Extension function to handle rate limit errors in ViewModels
 */
fun CoroutineScope.handleRateLimitError(
    error: Throwable,
    snackbarHostState: SnackbarHostState
) {
    if (error.isRateLimitError()) {
        launch {
            snackbarHostState.showSnackbar(
                message = "Rate limit reached. Please try again later.",
            )
        }
    }
} 