package com.nguyenmoclam.cocktailrecipes.data.common

/**
 * Enum representing different types of errors that can occur in the app
 */
enum class ErrorType {
    /**
     * Network-related errors: no connection, timeout, etc.
     */
    NETWORK,
    
    /**
     * Server errors (5xx status codes)
     */
    SERVER,
    
    /**
     * Client errors (4xx status codes)
     */
    CLIENT,
    
    /**
     * Generic error when no specific type can be determined
     */
    GENERIC,
    
    /**
     * Error when requested data is not found (404)
     */
    NOT_FOUND,
    
    /**
     * Authentication/Authorization errors (401, 403)
     */
    AUTH,
    
    /**
     * Service unavailable or rate limited (429, 503)
     */
    SERVICE_UNAVAILABLE
} 