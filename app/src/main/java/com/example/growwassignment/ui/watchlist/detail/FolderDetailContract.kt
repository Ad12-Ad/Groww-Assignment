package com.example.growwassignment.ui.watchlist.detail

import com.example.growwassignment.domain.model.SavedFund

data class FolderDetailState(
    val folderName: String = "",
    val funds: List<SavedFund> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface FolderDetailEvent {
    data class LoadFolder(val folderId: Int, val folderName: String) : FolderDetailEvent
    data class OnFundClicked(val schemeCode: Int) : FolderDetailEvent
    data class OnRemoveFundClicked(val schemeCode: Int, val folderId: Int) : FolderDetailEvent
    data object OnBackClicked : FolderDetailEvent
    data object OnExploreClicked : FolderDetailEvent
}

sealed interface FolderDetailEffect {
    data class NavigateToProduct(val schemeCode: Int) : FolderDetailEffect
    data object NavigateBack : FolderDetailEffect
    data object NavigateToExploreTab : FolderDetailEffect
}