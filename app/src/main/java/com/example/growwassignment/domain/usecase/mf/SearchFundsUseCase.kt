package com.example.growwassignment.domain.usecase.mf

import com.example.growwassignment.domain.model.FundSearch
import com.example.growwassignment.domain.repository.MfRepository
import com.example.growwassignment.domain.utils.Resource
import com.example.growwassignment.domain.utils.toResource
import javax.inject.Inject

class SearchFundsUseCase @Inject constructor(
    private val repository: MfRepository
) {
    suspend operator fun invoke(query: String): Resource<List<FundSearch>> {
        if (query.isBlank()) return Resource.Success(emptyList())
        return repository.searchFunds(query).toResource()
    }
}