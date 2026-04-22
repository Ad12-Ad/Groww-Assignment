package com.example.growwassignment.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.growwassignment.domain.usecase.mf.GetFundDetailsWithChartUseCase
import com.example.growwassignment.domain.usecase.watchlist.DeleteFundFromAllPortfoliosUseCase
import com.example.growwassignment.domain.usecase.watchlist.ObserveFundWatchlistStatusUseCase
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
class ProductViewModel @Inject constructor(
    private val getFundDetailsWithChart: GetFundDetailsWithChartUseCase,
    private val observeFundWatchlistStatus: ObserveFundWatchlistStatusUseCase,
    private val deleteFundFromAllPortfolios: DeleteFundFromAllPortfoliosUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProductState())
    val state: StateFlow<ProductState> = _state.asStateFlow()

    private val _effect = Channel<ProductEffect>()
    val effect = _effect.receiveAsFlow()

    private var currentSchemeCode: Int = -1

    fun onEvent(event: ProductEvent) {
        when (event) {
            is ProductEvent.LoadFund -> {
                if (currentSchemeCode != event.schemeCode) {
                    currentSchemeCode = event.schemeCode
                    fetchFundDetails(event.schemeCode, isRefresh = false)
                    observeWatchlistStatus(event.schemeCode)
                }
            }
            is ProductEvent.RefreshFund -> {
                if (currentSchemeCode != -1) {
                    fetchFundDetails(currentSchemeCode, isRefresh = true)
                }
            }
            is ProductEvent.OnAddToPortfolioClicked -> sendEffect(ProductEffect.ShowAddToWatchlistBottomSheet)
            is ProductEvent.OnBackClicked -> sendEffect(ProductEffect.NavigateBack)
            is ProductEvent.OnRemoveFromAllClicked -> {
                viewModelScope.launch {
                    val schemeCode = _state.value.fundDetails?.schemeCode ?: return@launch
                    deleteFundFromAllPortfolios(schemeCode)
                    sendEffect(ProductEffect.ShowToast("Removed from all portfolios"))
                }
            }
        }
    }

    private fun fetchFundDetails(schemeCode: Int, isRefresh: Boolean) {
        viewModelScope.launch {
            if (isRefresh) {
                _state.update { it.copy(isRefreshing = true, error = null) }
            } else {
                _state.update { it.copy(isLoading = true, error = null) }
            }

            when (val resource = getFundDetailsWithChart(schemeCode)) {
                is Resource.Success -> {
                    resource.data?.let { result ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                fundDetails = result.details,
                                chartData = result.chartData
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, isRefreshing = false, error = resource.message) }
                    if (isRefresh) {
                        sendEffect(ProductEffect.ShowToast(resource.message ?: "Failed to refresh"))
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun observeWatchlistStatus(schemeCode: Int) {
        viewModelScope.launch {
            observeFundWatchlistStatus(schemeCode).collect { isSaved ->
                _state.update { it.copy(isSavedInWatchlist = isSaved) }
            }
        }
    }

    private fun sendEffect(effect: ProductEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}