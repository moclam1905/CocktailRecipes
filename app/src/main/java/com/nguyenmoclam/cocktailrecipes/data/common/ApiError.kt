package com.nguyenmoclam.cocktailrecipes.data.common

/**
 * Data class representing an API error with comprehensive information
 *
 * @property message User-friendly error message
 * @property code HTTP status code or custom error code
 * @property errorType Type of error categorized by ErrorType enum
 * @property throwable Original exception if available
 * @property endpoint API endpoint that caused the error (optional)
 * @property timestamp When the error occurred
 * @property retryable Whether this error is likely to be resolved by retrying
 */
data class ApiError(
    val message: String,
    val code: Int? = null,
    val errorType: ErrorType = ErrorType.GENERIC,
    val throwable: Throwable? = null,
    val endpoint: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val retryable: Boolean = false
) {
    /**
     * Provide a user-friendly message based on error type
     */
    fun getUserFriendlyMessage(): String {
        return when (errorType) {
            ErrorType.NETWORK -> "Network connection error. Please check your internet connection and try again."
            ErrorType.SERVER -> "Server error. We're working on fixing this issue. Please try again later."
            ErrorType.CLIENT -> "Invalid request. Please try again or contact support if the issue persists."
            ErrorType.NOT_FOUND -> "The requested information could not be found."
            ErrorType.AUTH -> "Authentication error. Please log in again."
            ErrorType.SERVICE_UNAVAILABLE -> "Service temporarily unavailable. Please try again later."
            ErrorType.GENERIC -> message.ifBlank { "An unknown error occurred. Please try again." }
        }
    }
    
    /**
     * Determine if the error is likely transient and can be retried
     */
    fun isTransient(): Boolean {
        return retryable || errorType == ErrorType.NETWORK || errorType == ErrorType.SERVICE_UNAVAILABLE
    }
    
    companion object {
        /**
         * Create a network error
         */
        fun networkError(message: String = "Network connection error", throwable: Throwable? = null): ApiError {
            return ApiError(
                message = message,
                errorType = ErrorType.NETWORK,
                throwable = throwable,
                retryable = true
            )
        }
        
        /**
         * Create a server error
         */
        fun serverError(code: Int, message: String = "Server error", throwable: Throwable? = null): ApiError {
            return ApiError(
                message = message,
                code = code,
                errorType = ErrorType.SERVER,
                throwable = throwable,
                retryable = true
            )
        }
        
        /**
         * Create a not found error
         */
        fun notFoundError(message: String = "Resource not found"): ApiError {
            return ApiError(
                message = message,
                code = 404,
                errorType = ErrorType.NOT_FOUND,
                retryable = false
            )
        }
        
        /**
         * Create an authentication error
         */
        fun authError(message: String = "Authentication error", code: Int = 401): ApiError {
            return ApiError(
                message = message,
                code = code,
                errorType = ErrorType.AUTH,
                retryable = false
            )
        }
    }
} 