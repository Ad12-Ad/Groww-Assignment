package com.example.growwassignment.domain.usecase.mf

import com.example.growwassignment.domain.model.FundDetails
import com.example.growwassignment.domain.repository.MfRepository
import com.example.growwassignment.domain.utils.Resource
import com.example.growwassignment.domain.utils.toResource
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class FundDetailsResult(
    val details: FundDetails,
    val chartData: ChartEntryModel
)

class GetFundDetailsWithChartUseCase @Inject constructor(
    private val repository: MfRepository
) {
    suspend operator fun invoke(schemeCode: Int): Resource<FundDetailsResult> {
        val resource = repository.getFundDetails(schemeCode).toResource()

        return when (resource) {
            is Resource.Success -> {
                val details = resource.data
                if (details != null) {
                    val chartData = buildChartModel(details)
                    Resource.Success(FundDetailsResult(details, chartData))
                } else {
                    Resource.Error("Invalid fund data")
                }
            }
            is Resource.Error -> Resource.Error(resource.message ?: "Unknown error")
            is Resource.Loading -> Resource.Loading()
        }
    }

    private suspend fun buildChartModel(details: FundDetails): ChartEntryModel {
        return withContext(Dispatchers.Default) {
            val recentData = details.navHistory.take(250).reversed()
            val entries = recentData.mapIndexed { index, navData ->
                FloatEntry(x = index.toFloat(), y = navData.nav.toFloat())
            }
            entryModelOf(entries)
        }
    }
}