package com.example.growwassignment.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist_folders")
data class WatchlistFolderEntity(
    @PrimaryKey(autoGenerate = true) val folderId: Int = 0,
    val folderName: String,
    val createdAt: Long = System.currentTimeMillis()
)