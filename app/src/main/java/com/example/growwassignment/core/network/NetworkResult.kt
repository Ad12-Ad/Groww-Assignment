package com.example.growwassignment.core.network

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T): NetworkResult<T>()
    data class ApiError(val code: Int, val message: String): NetworkResult<Nothing>()
    data class NetworkError(val message: String): NetworkResult<Nothing>()
    data class UnknownError(val e: Throwable): NetworkResult<Nothing>()

    inline fun <R> map(transform: (T) -> R): NetworkResult<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is ApiError -> this
            is NetworkError -> this
            is UnknownError -> this
        }
    }
}