package com.nguyenmoclam.cocktailrecipes.data.common

import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Maps exceptions to ApiError objects with appropriate error types
 */
@Singleton
class ErrorMapper @Inject constructor() {
    
    /**
     * Convert a throwable to an ApiError with appropriate type and message
     */
    fun mapThrowableToApiError(throwable: Throwable, endpoint: String? = null): ApiError {
        return when (throwable) {
            // Network errors
            is UnknownHostException -> ApiError(
                message = "Unable to connect to server",
                errorType = ErrorType.NETWORK,
                throwable = throwable,
                endpoint = endpoint,
                retryable = true
            )
            
            is ConnectException -> ApiError(
                message = "Connection failed",
                errorType = ErrorType.NETWORK,
                throwable = throwable,
                endpoint = endpoint,
                retryable = true
            )
            
            is SocketTimeoutException -> ApiError(
                message = "Connection timeout",
                errorType = ErrorType.NETWORK,
                throwable = throwable,
                endpoint = endpoint,
                retryable = true
            )
            
            is IOException -> ApiError(
                message = "Network error: ${throwable.message ?: "Unknown network error"}",
                errorType = ErrorType.NETWORK,
                throwable = throwable,
                endpoint = endpoint,
                retryable = true
            )
            
            // HTTP errors
            is HttpException -> mapHttpExceptionToApiError(throwable, endpoint)
            
            // Default case for unknown errors
            else -> ApiError(
                message = throwable.message ?: "Unknown error",
                errorType = ErrorType.GENERIC,
                throwable = throwable,
                endpoint = endpoint,
                retryable = false
            )
        }
    }
    
    /**
     * Map HTTP exceptions to specific ApiError types based on status code
     */
    private fun mapHttpExceptionToApiError(exception: HttpException, endpoint: String?): ApiError {
        val code = exception.code()
        val errorType = when (code) {
            in 400..499 -> when (code) {
                401, 403 -> ErrorType.AUTH
                404 -> ErrorType.NOT_FOUND
                429 -> ErrorType.SERVICE_UNAVAILABLE
                else -> ErrorType.CLIENT
            }
            in 500..599 -> ErrorType.SERVER
            else -> ErrorType.GENERIC
        }
        
        val message = when (code) {
            401 -> "Unauthorized: Please log in again"
            403 -> "Forbidden: You don't have permission to access this resource"
            404 -> "Not found: The requested resource doesn't exist"
            429 -> "Too many requests: Please try again later"
            in 500..599 -> "Server error: Please try again later"
            else -> exception.message()
        }
        
        // Determine if this error is likely to be resolved by retry
        val retryable = code == 429 || code >= 500
        
        return ApiError(
            message = message,
            code = code,
            errorType = errorType,
            throwable = exception,
            endpoint = endpoint,
            retryable = retryable
        )
    }
} 