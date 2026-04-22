package com.example.growwassignment.ui.product.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.growwassignment.domain.model.SavedFund
import com.example.growwassignment.domain.usecase.watchlist.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistSheetViewModel @Inject constructor(
    private val observeAllFolders: ObserveAllFoldersUseCase,
    private val observeFundsInFolder: ObserveFundsInFolderUseCase,
    private val createFolderUseCase: CreateFolderUseCase,
    private val toggleFundInWatchlist: ToggleFundInWatchlistUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(WatchlistSheetState())
    val state: StateFlow<WatchlistSheetState> = _state.asStateFlow()

    private val _effect = Channel<WatchlistSheetEffect>()
    val effect = _effect.receiveAsFlow()

    private var currentSchemeCode: Int = -1

    fun onEvent(event: WatchlistSheetEvent) {
        when (event) {
            is WatchlistSheetEvent.LoadWatchlistData -> {
                if (currentSchemeCode != event.schemeCode) {
                    currentSchemeCode = event.schemeCode
                    observeFoldersAndStatus(event.schemeCode)
                }
            }
            is WatchlistSheetEvent.OnNewFolderNameChanged -> {
                _state.update { it.copy(newFolderNameInput = event.name) }
            }
            is WatchlistSheetEvent.OnCreateFolderClicked -> createFolder()
            is WatchlistSheetEvent.OnToggleFolder -> toggleFund(event)
        }
    }

    private fun observeFoldersAndStatus(schemeCode: Int) {
        viewModelScope.launch {
            // Senior engineering approach: Use flatMapLatest and combine to sync multiple flows
            observeAllFolders().flatMapLatest { folders ->
                _state.update { it.copy(folders = folders) }

                if (folders.isEmpty()) return@flatMapLatest flowOf(emptySet<Int>())

                // Create a list of flows: each checking if the fund is in that specific folder
                val folderStatusFlows = folders.map { folder ->
                    observeFundsInFolder(folder.folderId).map { funds ->
                        if (funds.any { it.schemeCode == schemeCode }) folder.folderId else null
                    }
                }

                combine(folderStatusFlows) { statuses ->
                    statuses.filterNotNull().toSet()
                }
            }.collect { activeIds ->
                _state.update { it.copy(activeFolderIds = activeIds) }
            }
        }
    }

    private fun createFolder() {
        val folderName = _state.value.newFolderNameInput.trim()
        if (folderName.isEmpty()) {
            sendEffect(WatchlistSheetEffect.ShowToast("Folder name cannot be empty"))
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isCreatingFolder = true) }
            createFolderUseCase(folderName)
            _state.update { it.copy(isCreatingFolder = false, newFolderNameInput = "") }
            sendEffect(WatchlistSheetEffect.ShowToast("Portfolio '$folderName' created"))
        }
    }

    private fun toggleFund(event: WatchlistSheetEvent.OnToggleFolder) {
        viewModelScope.launch {
            val fund = SavedFund(
                schemeCode = event.schemeCode,
                schemeName = event.schemeName,
                folderId = event.folderId,
                latestNav = event.latestNav
            )
            toggleFundInWatchlist(fund, event.isChecked)
        }
    }

    private fun sendEffect(effect: WatchlistSheetEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}