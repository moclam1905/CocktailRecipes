package com.nguyenmoclam.cocktailrecipes.data.common

import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min
import kotlin.math.pow

/**
 * Handles retry logic for API calls with exponential backoff
 */
@Singleton
class RetryHandler @Inject constructor(
    private val networkMonitor: NetworkMonitor
) {
    /**
     * Retry configuration
     */
    companion object {
        private const val MAX_RETRIES = 3
        private const val INITIAL_DELAY_MS = 1000L  // 1 second
        private const val MAX_DELAY_MS = 10000L     // 10 seconds
        private const val BACKOFF_MULTIPLIER = 2.0  // Exponential backoff factor
    }
    
    /**
     * Execute a suspending operation with retry capability for transient errors
     * 
     * @param T The return type of the operation
     * @param operation The suspending function to execute
     * @param shouldRetry Optional predicate to determine if an error should be retried
     * @return Result of the operation
     */
    suspend fun <T> executeWithRetry(
        operation: suspend () -> T,
        shouldRetry: (Throwable) -> Boolean = { isTransientError(it) }
    ): Result<T> {
        var currentDelay = INITIAL_DELAY_MS
        var attemptCount = 0
        
        while (true) {
            try {
                return Result.success(operation())
            } catch (e: Throwable) {
                attemptCount++
                
                // Don't retry if we've reached max retries or if the error isn't considered retryable
                if (attemptCount >= MAX_RETRIES || !shouldRetry(e)) {
                    return Result.failure(e)
                }
                
                // Wait before retrying
                delay(currentDelay)
                
                // Increase delay for next attempt with exponential backoff
                currentDelay = min(
                    (currentDelay * BACKOFF_MULTIPLIER).toLong(),
                    MAX_DELAY_MS
                )
            }
        }
    }
    
    /**
     * Determine if an error is transient (temporary) and should be retried
     */
    private fun isTransientError(throwable: Throwable): Boolean {
        // If the error is wrapped in ApiError, use its retryable property
        return if (throwable is ApiErrorException) {
            throwable.apiError.isTransient()
        } else {
            // Otherwise, check network availability as most errors during offline state 
            // will be resolved when network is back
            return networkMonitor.isNetworkAvailable()
        }
    }
}

/**
 * Custom exception that wraps an ApiError
 */
class ApiErrorException(val apiError: ApiError) : Exception(apiError.message) 