package com.example.growwassignment.domain.usecase.mf

import com.example.growwassignment.domain.model.FundSearch
import com.example.growwassignment.domain.utils.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

data class ExploreCategoriesResult(
    val indexFunds: List<FundSearch> = emptyList(),
    val bluechipFunds: List<FundSearch> = emptyList(),
    val largeCapFunds: List<FundSearch> = emptyList(),
    val taxSaverFunds: List<FundSearch> = emptyList()
)

class GetExploreCategoriesUseCase @Inject constructor(
    private val searchFunds: SearchFundsUseCase
) {
    suspend operator fun invoke(): Resource<ExploreCategoriesResult> = coroutineScope {
        val indexDeferred = async { searchFunds("index") }
        val bluechipDeferred = async { searchFunds("bluechip") }
        val largeCapDeferred = async { searchFunds("large cap") }
        val taxSaverDeferred = async { searchFunds("tax") }

        val indexRes = indexDeferred.await()

        if (indexRes is Resource.Error) {
            return@coroutineScope Resource.Error(indexRes.message ?: "Failed to load funds")
        }

        Resource.Success(
            ExploreCategoriesResult(
                indexFunds = extract(indexRes),
                bluechipFunds = extract(bluechipDeferred.await()),
                largeCapFunds = extract(largeCapDeferred.await()),
                taxSaverFunds = extract(taxSaverDeferred.await())
            )
        )
    }

    private fun extract(resource: Resource<List<FundSearch>>): List<FundSearch> {
        return resource.data?.take(4) ?: emptyList()
    }
}