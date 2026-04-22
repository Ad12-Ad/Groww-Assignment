package com.example.growwassignment.domain.model

data class FundSearch(
    val schemeCode: Int,
    val schemeName: String
)

data class FundDetails(
    val fundHouse: String,
    val schemeType: String,
    val schemeCategory: String,
    val schemeCode: Int,
    val schemeName: String,
    val navHistory: List<NavData>
)

data class NavData(
    val date: String,
    val nav: Double
)

data class WatchlistFolder(
    val folderId: Int,
    val folderName: String
)

data class SavedFund(
    val schemeCode: Int,
    val schemeName: String,
    val folderId: Int,
    val latestNav: String
)