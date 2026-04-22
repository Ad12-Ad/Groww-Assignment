package com.example.growwassignment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.growwassignment.data.local.entity.SavedFundEntity
import com.example.growwassignment.data.local.entity.WatchlistFolderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: WatchlistFolderEntity)

    @Query("SELECT * FROM watchlist_folders ORDER BY createdAt DESC")
    fun getAllFolders(): Flow<List<WatchlistFolderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFundToWatchlist(fund: SavedFundEntity)

    @Query("DELETE FROM saved_funds WHERE schemeCode = :schemeCode AND folderId = :folderId")
    suspend fun removeFundFromWatchlist(schemeCode: Int, folderId: Int)

    @Query("SELECT * FROM saved_funds WHERE folderId = :folderId ORDER BY addedAt DESC")
    fun getFundsInFolder(folderId: Int): Flow<List<SavedFundEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_funds WHERE schemeCode = :schemeCode)")
    fun isFundInAnyWatchlist(schemeCode: Int): Flow<Boolean>

    @Query("DELETE FROM saved_funds WHERE schemeCode = :schemeCode")
    suspend fun deleteFundFromAllWatchlists(schemeCode: Int)
}