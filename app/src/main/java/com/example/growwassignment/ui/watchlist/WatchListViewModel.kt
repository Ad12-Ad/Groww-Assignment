package com.example.growwassignment.ui.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.growwassignment.domain.usecase.watchlist.ObserveAllFoldersUseCase
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
class WatchlistViewModel @Inject constructor(
    private val observeAllFolders: ObserveAllFoldersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(WatchlistState())
    val state: StateFlow<WatchlistState> = _state.asStateFlow()

    private val _effect = Channel<WatchlistEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        observeFolders()
    }

    private fun observeFolders() {
        viewModelScope.launch {
            observeAllFolders().collect { folders ->
                _state.update { it.copy(folders = folders, isLoading = false) }
            }
        }
    }

    fun onEvent(event: WatchlistEvent) {
        when (event) {
            is WatchlistEvent.OnFolderClicked -> {
                sendEffect(WatchlistEffect.NavigateToFolderDetail(event.folderId, event.folderName))
            }
        }
    }

    private fun sendEffect(effect: WatchlistEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}