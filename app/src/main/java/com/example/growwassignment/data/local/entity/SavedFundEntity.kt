package com.example.growwassignment.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "saved_funds",
    primaryKeys = ["schemeCode", "folderId"]
)
data class SavedFundEntity(
    val schemeCode: Int,
    val schemeName: String,
    val folderId: Int,
    val latestNav: String,
    val addedAt: Long = System.currentTimeMillis()
)