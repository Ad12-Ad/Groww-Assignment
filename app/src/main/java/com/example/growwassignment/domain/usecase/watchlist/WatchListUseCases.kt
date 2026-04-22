package com.example.growwassignment.domain.usecase.watchlist

import com.example.growwassignment.domain.model.SavedFund
import com.example.growwassignment.domain.repository.WatchlistRepository
import javax.inject.Inject

class ObserveAllFoldersUseCase @Inject constructor(private val repo: WatchlistRepository) {
    operator fun invoke() = repo.getAllFolders()
}

class CreateFolderUseCase @Inject constructor(private val repo: WatchlistRepository) {
    suspend operator fun invoke(name: String) {
        if (name.isNotBlank()) repo.createFolder(name.trim())
    }
}

class ObserveFundsInFolderUseCase @Inject constructor(private val repo: WatchlistRepository) {
    operator fun invoke(folderId: Int) = repo.getFundsInFolder(folderId)
}

class ToggleFundInWatchlistUseCase @Inject constructor(private val repo: WatchlistRepository) {
    suspend operator fun invoke(fund: SavedFund, isAdding: Boolean) {
        if (isAdding) repo.addFundToWatchlist(fund)
        else repo.removeFundFromWatchlist(fund.schemeCode, fund.folderId)
    }
}

class ObserveFundWatchlistStatusUseCase @Inject constructor(private val repo: WatchlistRepository) {
    operator fun invoke(schemeCode: Int) = repo.isFundInAnyWatchlist(schemeCode)
}