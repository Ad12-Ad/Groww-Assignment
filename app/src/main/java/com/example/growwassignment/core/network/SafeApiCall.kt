package com.example.growwassignment.core.network

import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<T>
): NetworkResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful && response.body() != null) {
            NetworkResult.Success(response.body()!!)
        } else {
            // Prefer the actual error body from the server if it exists
            val errorMsg = response.errorBody()?.string() ?: response.message()
            NetworkResult.ApiError(code = response.code(), message = errorMsg)
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: IOException) {
        NetworkResult.NetworkError("No internet connection. Please check your network.")
    } catch (e: Exception) {
        NetworkResult.UnknownError(e)
    }
}