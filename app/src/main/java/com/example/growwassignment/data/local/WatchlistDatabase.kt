package com.example.growwassignment.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.growwassignment.data.local.dao.WatchlistDao
import com.example.growwassignment.data.local.entity.SavedFundEntity
import com.example.growwassignment.data.local.entity.WatchlistFolderEntity

@Database(
    entities = [WatchlistFolderEntity::class, SavedFundEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WatchlistDatabase : RoomDatabase() {
    abstract val watchlistDao: WatchlistDao

    companion object {
        const val DATABASE_NAME = "mf_watchlist_db"
    }
}