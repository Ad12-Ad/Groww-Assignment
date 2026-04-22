package com.example.growwassignment.ui.watchlist

import com.example.growwassignment.domain.model.WatchlistFolder

data class WatchlistState(
    val folders: List<WatchlistFolder> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface WatchlistEvent {
    data class OnFolderClicked(val folderId: Int, val folderName: String) : WatchlistEvent
}

sealed interface WatchlistEffect {
    data class NavigateToFolderDetail(val folderId: Int, val folderName: String) : WatchlistEffect
}