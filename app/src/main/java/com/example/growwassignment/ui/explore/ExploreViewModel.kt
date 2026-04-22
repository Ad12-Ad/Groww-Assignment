package com.example.growwassignment.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.growwassignment.domain.model.FundSearch
import com.example.growwassignment.domain.usecase.mf.GetExploreCategoriesUseCase
import com.example.growwassignment.domain.utils.Resource
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
class ExploreViewModel @Inject constructor(
    private val getExploreCategories: GetExploreCategoriesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ExploreState())
    val state: StateFlow<ExploreState> = _state.asStateFlow()

    private val _effect = Channel<ExploreEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        onEvent(ExploreEvent.LoadCategories)
    }

    fun onEvent(event: ExploreEvent) {
        when (event) {
            is ExploreEvent.LoadCategories -> loadExploreData(isRefresh = false)
            is ExploreEvent.RefreshCategories -> loadExploreData(isRefresh = true)
            is ExploreEvent.OnFundClicked -> sendEffect(ExploreEffect.NavigateToProduct(event.schemeCode))
            is ExploreEvent.OnSearchClicked -> sendEffect(ExploreEffect.NavigateToSearch)
            is ExploreEvent.OnViewAllClicked -> sendEffect(ExploreEffect.NavigateToViewAll(event.categoryQuery))
        }
    }

    private fun loadExploreData(isRefresh: Boolean) {
        viewModelScope.launch {
            if (isRefresh) {
                _state.update { it.copy(isRefreshing = true, error = null) }
            } else {
                _state.update { it.copy(isLoading = true, error = null) }
            }

            when (val resource = getExploreCategories()) {
                is Resource.Success -> {
                    resource.data?.let { data ->
                        _state.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                isRefreshing = false,
                                indexFunds = data.indexFunds,
                                bluechipFunds = data.bluechipFunds,
                                largeCapFunds = data.largeCapFunds,
                                taxSaverFunds = data.taxSaverFunds
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(isLoading = false, isRefreshing = false, error = resource.message)
                    }
                    if (isRefresh) {
                        sendEffect(ExploreEffect.ShowToast(resource.message ?: "Failed to refresh"))
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun sendEffect(effect: ExploreEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}