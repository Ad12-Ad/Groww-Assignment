package com.example.growwassignment.domain.repository

import com.example.growwassignment.domain.model.SavedFund
import com.example.growwassignment.domain.model.WatchlistFolder
import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {
    fun getAllFolders(): Flow<List<WatchlistFolder>>
    suspend fun createFolder(name: String)
    suspend fun addFundToWatchlist(fund: SavedFund)
    suspend fun removeFundFromWatchlist(schemeCode: Int, folderId: Int)
    fun getFundsInFolder(folderId: Int): Flow<List<SavedFund>>
    fun isFundInAnyWatchlist(schemeCode: Int): Flow<Boolean>
    suspend fun removeFromAllWatchlists(schemeCode: Int)
}