package com.example.growwassignment.data.remote

import com.example.growwassignment.data.remote.dto.FundDetailsDto
import com.example.growwassignment.data.remote.dto.FundSearchDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MfApi {
    @GET("mf/search")
    suspend fun searchFunds(
        @Query("q") query: String
    ): Response<List<FundSearchDto>>

    @GET("mf/{scheme_code}")
    suspend fun getFundDetails(
        @Path("scheme_code") schemeCode: Int
    ): Response<FundDetailsDto>
}