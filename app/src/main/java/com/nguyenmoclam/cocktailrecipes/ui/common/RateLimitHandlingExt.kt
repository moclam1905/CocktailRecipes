package com.nguyenmoclam.cocktailrecipes.ui.common

import android.content.Context
import android.widget.Toast
import com.nguyenmoclam.cocktailrecipes.data.common.ApiCallException

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