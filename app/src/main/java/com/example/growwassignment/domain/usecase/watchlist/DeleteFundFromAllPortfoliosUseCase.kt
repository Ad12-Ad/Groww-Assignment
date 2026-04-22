package com.example.growwassignment.domain.usecase.watchlist

import com.example.growwassignment.domain.repository.WatchlistRepository
import javax.inject.Inject

class DeleteFundFromAllPortfoliosUseCase @Inject constructor(
    private val repository: WatchlistRepository
) {
    suspend operator fun invoke(schemeCode: Int) {
        repository.removeFromAllWatchlists(schemeCode)
    }
}