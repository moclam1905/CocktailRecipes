package com.nguyenmoclam.cocktailrecipes.data.remote.interceptor

import android.util.Log
import com.nguyenmoclam.cocktailrecipes.data.common.ApiCallException
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min
import kotlin.math.pow

/**
 * Interceptor to handle API rate limiting.
 * - Tracks the number of API calls and their timestamps
 * - Implements exponential backoff for rate limit responses (429)
 * - Respects rate limit headers from API responses
 */
@Singleton
class RateLimitInterceptor @Inject constructor() : Interceptor {
    
    private val TAG = "RateLimitInterceptor"
    
    // Track recent API calls with timestamps
    private val apiCallHistory = mutableListOf<Long>()
    
    // Rate limit configuration
    private var maxCallsPerMinute = 10 // Default conservative value
    private var retryCount = 0
    private val maxRetries = 3
    
    // Headers that might be sent by the API
    private val remainingHeader = "X-RateLimit-Remaining" // Common header for remaining calls
    private val resetHeader = "X-RateLimit-Reset" // Common header for reset time
    private val limitHeader = "X-RateLimit-Limit" // Common header for limit value
    
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // Clean up old call history (older than 1 minute)
        synchronized(apiCallHistory) {
            val nowMillis = System.currentTimeMillis()
            apiCallHistory.removeAll { timestamp ->
                nowMillis - timestamp > TimeUnit.MINUTES.toMillis(1)
            }
            
            // Check if we're making too many calls
            if (apiCallHistory.size >= maxCallsPerMinute) {
                val waitTimeMs = calculateBackoffTime()
                if (retryCount >= maxRetries) {
                    retryCount = 0 // Reset for next time
                    throw ApiCallException("Rate limit exceeded. Please try again later.")
                }
                
                retryCount++
                Timber.tag(TAG).w("Rate limit approaching, backing off for $waitTimeMs ms")
                try {
                    Thread.sleep(waitTimeMs)
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    throw IOException("Rate limit backoff interrupted", e)
                }
            }
            
            // Add this call to history
            apiCallHistory.add(nowMillis)
        }
        
        // Proceed with the call
        val response = chain.proceed(request)
        
        // Update rate limit information from headers if available
        response.headers.let { headers ->
            headers[remainingHeader]?.toIntOrNull()?.let { remaining ->
                if (remaining <= 3) { // Getting close to limit
                    Timber.tag(TAG).w("API rate limit warning: $remaining calls remaining")
                }
            }
            
            headers[limitHeader]?.toIntOrNull()?.let { limit ->
                if (limit > 0 && limit != maxCallsPerMinute) {
                    maxCallsPerMinute = limit
                    Timber.tag(TAG).d("Updated rate limit to: $maxCallsPerMinute")
                }
            }
        }
        
        // Handle 429 Too Many Requests status code
        if (response.code == 429) {
            response.close()
            
            val retryAfterHeader = response.headers["Retry-After"]
            val waitTimeMs = if (!retryAfterHeader.isNullOrEmpty()) {
                // Server told us how long to wait (usually in seconds)
                TimeUnit.SECONDS.toMillis(retryAfterHeader.toLong())
            } else {
                // No specific guidance, use exponential backoff
                calculateBackoffTime()
            }
            
            if (retryCount >= maxRetries) {
                retryCount = 0 // Reset for next time
                throw ApiCallException("Rate limit exceeded. Please try again later.")
            }
            
            retryCount++
            Timber.tag(TAG).w("Rate limit hit (429), backing off for $waitTimeMs ms")
            try {
                Thread.sleep(waitTimeMs)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                throw IOException("Rate limit backoff interrupted", e)
            }
            
            // Retry the request
            return intercept(chain)
        }
        
        // Reset retry count on successful responses
        if (response.isSuccessful) {
            retryCount = 0
        }
        
        return response
    }
    
    /**
     * Calculate exponential backoff time with jitter
     * Uses the formula: min(maxBackoff, baseBackoff * 2^attempt) + random jitter
     */
    private fun calculateBackoffTime(): Long {
        val baseBackoff = 1000L // 1 second base
        val maxBackoff = 60000L // 1 minute max
        
        // Exponential backoff with jitter
        val exponentialPart = baseBackoff * 2.0.pow(retryCount.toDouble()).toLong()
        val backoffMs = min(maxBackoff, exponentialPart)
        
        // Add some randomness (jitter) to prevent synchronized retries
        val jitterMs = (Math.random() * 1000).toLong()
        
        return backoffMs + jitterMs
    }
} 