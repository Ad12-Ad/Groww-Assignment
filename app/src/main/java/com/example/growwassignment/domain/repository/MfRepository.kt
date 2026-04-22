package com.example.growwassignment.domain.repository

import com.example.growwassignment.core.network.NetworkResult
import com.example.growwassignment.domain.model.FundDetails
import com.example.growwassignment.domain.model.FundSearch

interface MfRepository {
    suspend fun searchFunds(query: String): NetworkResult<List<FundSearch>>
    suspend fun getFundDetails(schemeCode: Int): NetworkResult<FundDetails>
}