package com.example.growwassignment.ui.watchlist.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.growwassignment.domain.usecase.watchlist.ObserveFundsInFolderUseCase
import com.example.growwassignment.domain.usecase.watchlist.ToggleFundInWatchlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderDetailViewModel @Inject constructor(
    private val observeFundsInFolder: ObserveFundsInFolderUseCase,
    private val toggleFundInWatchlist: ToggleFundInWatchlistUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FolderDetailState())
    val state: StateFlow<FolderDetailState> = _state.asStateFlow()

    private val _effect = Channel<FolderDetailEffect>()
    val effect = _effect.receiveAsFlow()

    private var currentFolderId: Int = -1

    fun onEvent(event: FolderDetailEvent) {
        when (event) {
            is FolderDetailEvent.LoadFolder -> {
                if (currentFolderId != event.folderId) {
                    currentFolderId = event.folderId
                    _state.update { it.copy(folderName = event.folderName) }
                    observeFunds(event.folderId)
                }
            }
            is FolderDetailEvent.OnFundClicked -> sendEffect(FolderDetailEffect.NavigateToProduct(event.schemeCode))
            is FolderDetailEvent.OnBackClicked -> sendEffect(FolderDetailEffect.NavigateBack)
            is FolderDetailEvent.OnExploreClicked -> sendEffect(FolderDetailEffect.NavigateToExploreTab)
            is FolderDetailEvent.OnRemoveFundClicked -> {
                viewModelScope.launch {
                    val fundToRemove = _state.value.funds.firstOrNull { it.schemeCode == event.schemeCode }
                    if (fundToRemove != null) {
                        toggleFundInWatchlist(fundToRemove, isAdding = false)
                    }
                }
            }
        }
    }

    private fun observeFunds(folderId: Int) {
        viewModelScope.launch {
            observeFundsInFolder(folderId).collect { funds ->
                _state.update { it.copy(funds = funds, isLoading = false) }
            }
        }
    }

    private fun sendEffect(effect: FolderDetailEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}