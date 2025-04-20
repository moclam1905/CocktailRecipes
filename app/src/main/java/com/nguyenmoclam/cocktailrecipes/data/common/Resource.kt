package com.nguyenmoclam.cocktailrecipes.data.common

/**
 * Represents a resource that comes from a network request or local database.
 * Generic wrapper for data, loading status, or errors.
 */
sealed class Resource<out T> {
    /**
     * Successful response with data
     */
    data class Success<T>(val data: T) : Resource<T>()
    
    /**
     * Error response with detailed error information
     */
    data class Error(val apiError: ApiError) : Resource<Nothing>()
    
    /**
     * Loading state indicating request is in progress
     */
    data object Loading : Resource<Nothing>()

    /**
     * Helper methods to check the resource state
     */
    val isSuccess get() = this is Success
    val isError get() = this is Error
    val isLoading get() = this is Loading
    
    /**
     * Get data if Success, or null if Error or Loading
     */
    fun dataOrNull(): T? = if (this is Success) data else null
    
    /**
     * Get error if Error, or null otherwise
     */
    fun errorOrNull(): ApiError? = if (this is Error) apiError else null

    companion object {
        /**
         * Create a success resource with data
         */
        fun <T> success(data: T): Resource<T> = Success(data)
        
        /**
         * Create an error resource with ApiError
         */
        fun error(apiError: ApiError): Resource<Nothing> = Error(apiError)
        
        /**
         * Create an error resource with message and code
         */
        fun error(message: String, code: Int? = null): Resource<Nothing> = 
            Error(ApiError(message = message, code = code))
        
        /**
         * Create an error resource from a throwable
         */
        fun error(throwable: Throwable): Resource<Nothing> = 
            Error(ApiError(message = throwable.message ?: "Unknown error", throwable = throwable))
        
        /**
         * Create a loading resource
         */
        fun loading(): Resource<Nothing> = Loading
    }
} 