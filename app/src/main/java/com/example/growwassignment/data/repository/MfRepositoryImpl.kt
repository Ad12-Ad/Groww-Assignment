package com.example.growwassignment.data.repository

import com.example.growwassignment.core.network.NetworkResult
import com.example.growwassignment.core.network.safeApiCall
import com.example.growwassignment.data.mapper.toDomain
import com.example.growwassignment.data.remote.MfApi
import com.example.growwassignment.domain.model.FundDetails
import com.example.growwassignment.domain.model.FundSearch
import com.example.growwassignment.domain.repository.MfRepository

class MfRepositoryImpl(
    private val api: MfApi
) : MfRepository {

    override suspend fun searchFunds(query: String): NetworkResult<List<FundSearch>> {
        return safeApiCall { api.searchFunds(query) }.map { dtoList ->
            dtoList.map { it.toDomain() }
        }
    }

    override suspend fun getFundDetails(schemeCode: Int): NetworkResult<FundDetails> {
        return safeApiCall { api.getFundDetails(schemeCode) }.map { dto ->
            dto.toDomain()
        }
    }
}