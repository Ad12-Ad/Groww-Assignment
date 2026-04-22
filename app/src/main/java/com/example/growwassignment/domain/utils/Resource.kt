package com.example.growwassignment.domain.utils

import com.example.growwassignment.core.network.NetworkResult

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}

fun <T> NetworkResult<T>.toResource(): Resource<T> {
    return when (this) {
        is NetworkResult.Success -> Resource.Success(this.data)
        is NetworkResult.ApiError -> Resource.Error(this.message)
        is NetworkResult.NetworkError -> Resource.Error(this.message)
        is NetworkResult.UnknownError -> Resource.Error(this.e.localizedMessage ?: "An unexpected error occurred")
    }
}