package com.example.growwassignment.ui.product.watchlist

import com.example.growwassignment.domain.model.WatchlistFolder

data class WatchlistSheetState(
    val folders: List<WatchlistFolder> = emptyList(),
    val activeFolderIds: Set<Int> = emptySet(),
    val newFolderNameInput: String = "",
    val isCreatingFolder: Boolean = false
)

sealed interface WatchlistSheetEvent {
    data class LoadWatchlistData(val schemeCode: Int) : WatchlistSheetEvent
    data class OnNewFolderNameChanged(val name: String) : WatchlistSheetEvent
    data object OnCreateFolderClicked : WatchlistSheetEvent
    data class OnToggleFolder(
        val folderId: Int,
        val isChecked: Boolean,
        val schemeCode: Int,
        val schemeName: String,
        val latestNav: String
    ) : WatchlistSheetEvent
}

sealed interface WatchlistSheetEffect {
    data class ShowToast(val message: String) : WatchlistSheetEffect
}