package com.example.growwassignment.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.growwassignment.domain.repository.MfRepository
import com.example.growwassignment.domain.usecase.mf.SearchFundsUseCase
import com.example.growwassignment.domain.utils.Resource
import com.example.growwassignment.domain.utils.toResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchFunds: SearchFundsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val _effect = Channel<SearchEffect>()
    val effect = _effect.receiveAsFlow()

    private val _searchQueryFlow = MutableStateFlow("")
    private var isInitialized = false

    init {
        observeSearchQuery()
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.InitializeSearch -> {
                if (!isInitialized) {
                    isInitialized = true
                    if (event.initialQuery != null) {
                        _state.update {
                            it.copy(isViewAllMode = true, viewAllCategoryTitle = event.categoryTitle ?: "Funds")
                        }
                        _searchQueryFlow.value = event.initialQuery
                    } else {
                        _state.update { it.copy(isViewAllMode = false) }
                    }
                }
            }
            is SearchEvent.OnQueryChanged -> {
                if (!_state.value.isViewAllMode) {
                    _state.update { it.copy(query = event.query) }
                    _searchQueryFlow.value = event.query
                }
            }
            is SearchEvent.OnFundClicked -> sendEffect(SearchEffect.NavigateToProduct(event.schemeCode))
            is SearchEvent.OnBackClicked -> sendEffect(SearchEffect.NavigateBack)
            is SearchEvent.ClearSearch -> {
                _state.update { it.copy(query = "", results = emptyList(), error = null) }
                _searchQueryFlow.value = ""
            }
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQueryFlow
                .debounce(300L)
                .distinctUntilChanged()
                .filter { it.isNotBlank() }
                .mapLatest { query ->
                    _state.update { it.copy(isLoading = true, error = null) }
                    searchFunds(query)
                }
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    results = resource.data ?: emptyList(),
                                    error = if (resource.data.isNullOrEmpty()) "No funds found." else null
                                )
                            }
                        }
                        is Resource.Error -> {
                            _state.update {
                                it.copy(isLoading = false, error = resource.message, results = emptyList())
                            }
                        }
                        is Resource.Loading -> Unit
                    }
                }
        }
    }

    private fun sendEffect(effect: SearchEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}