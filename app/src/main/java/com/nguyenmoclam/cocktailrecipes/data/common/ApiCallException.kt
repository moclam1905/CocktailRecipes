package com.nguyenmoclam.cocktailrecipes.data.common

import java.io.IOException

/**
 * Custom exception for API call errors, including rate limiting
 */
class ApiCallException(message: String, cause: Throwable? = null) : IOException(message, cause) 