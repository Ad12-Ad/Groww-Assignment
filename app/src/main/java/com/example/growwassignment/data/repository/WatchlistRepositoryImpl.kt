package com.example.growwassignment.data.repository

import com.example.growwassignment.data.local.dao.WatchlistDao
import com.example.growwassignment.data.local.entity.WatchlistFolderEntity
import com.example.growwassignment.data.mapper.toDomain
import com.example.growwassignment.data.mapper.toEntity
import com.example.growwassignment.domain.model.SavedFund
import com.example.growwassignment.domain.model.WatchlistFolder
import com.example.growwassignment.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WatchlistRepositoryImpl(
    private val dao: WatchlistDao
) : WatchlistRepository {

    override fun getAllFolders(): Flow<List<WatchlistFolder>> {
        return dao.getAllFolders().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun createFolder(name: String) {
        dao.insertFolder(WatchlistFolderEntity(folderName = name))
    }

    override suspend fun addFundToWatchlist(fund: SavedFund) {
        dao.insertFundToWatchlist(fund.toEntity())
    }

    override suspend fun removeFundFromWatchlist(schemeCode: Int, folderId: Int) {
        dao.removeFundFromWatchlist(schemeCode, folderId)
    }

    override fun getFundsInFolder(folderId: Int): Flow<List<SavedFund>> {
        return dao.getFundsInFolder(folderId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun isFundInAnyWatchlist(schemeCode: Int): Flow<Boolean> {
        return dao.isFundInAnyWatchlist(schemeCode)
    }

    override suspend fun removeFromAllWatchlists(schemeCode: Int) {
        dao.deleteFundFromAllWatchlists(schemeCode)
    }
}