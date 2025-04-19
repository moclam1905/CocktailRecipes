package com.nguyenmoclam.cocktailrecipes.data.common

/**
 * Represents a resource that comes from a network request or local database.
 */
sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String, val code: Int? = null) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()

    companion object {
        fun <T> success(data: T): Resource<T> = Success(data)
        fun error(message: String, code: Int? = null): Resource<Nothing> = Error(message, code)
        fun loading(): Resource<Nothing> = Loading
    }
} 