package com.example.growwassignment.data.mapper

import com.example.growwassignment.data.local.entity.SavedFundEntity
import com.example.growwassignment.data.local.entity.WatchlistFolderEntity
import com.example.growwassignment.data.remote.dto.FundDetailsDto
import com.example.growwassignment.data.remote.dto.FundSearchDto
import com.example.growwassignment.domain.model.FundDetails
import com.example.growwassignment.domain.model.FundSearch
import com.example.growwassignment.domain.model.NavData
import com.example.growwassignment.domain.model.SavedFund
import com.example.growwassignment.domain.model.WatchlistFolder

fun FundSearchDto.toDomain(): FundSearch {
    return FundSearch(
        schemeCode = this.schemeCode,
        schemeName = this.schemeName
    )
}

fun FundDetailsDto.toDomain(): FundDetails {
    return FundDetails(
        fundHouse = this.meta.fundHouse,
        schemeType = this.meta.schemeType,
        schemeCategory = this.meta.schemeCategory,
        schemeCode = this.meta.schemeCode,
        schemeName = this.meta.schemeName,
        navHistory = this.data.mapNotNull {
            try {
                NavData(date = it.date, nav = it.nav.toDouble())
            } catch (e: NumberFormatException) {
                null
            }
        }
    )
}

fun WatchlistFolderEntity.toDomain(): WatchlistFolder {
    return WatchlistFolder(
        folderId = this.folderId,
        folderName = this.folderName
    )
}

fun SavedFundEntity.toDomain(): SavedFund {
    return SavedFund(
        schemeCode = this.schemeCode,
        schemeName = this.schemeName,
        folderId = this.folderId,
        latestNav = this.latestNav
    )
}

fun SavedFund.toEntity(): SavedFundEntity {
    return SavedFundEntity(
        schemeCode = this.schemeCode,
        schemeName = this.schemeName,
        folderId = this.folderId,
        latestNav = this.latestNav
    )
}