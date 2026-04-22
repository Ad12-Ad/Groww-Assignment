package com.example.growwassignment.ui.product

import com.example.growwassignment.domain.model.FundDetails
import com.patrykandpatrick.vico.core.entry.ChartEntryModel

data class ProductState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val fundDetails: FundDetails? = null,
    val chartData: ChartEntryModel? = null,
    val isSavedInWatchlist: Boolean = false
)

sealed interface ProductEvent {
    data class LoadFund(val schemeCode: Int) : ProductEvent
    data object RefreshFund : ProductEvent
    data object OnAddToPortfolioClicked : ProductEvent
    data object OnBackClicked : ProductEvent
    data object OnRemoveFromAllClicked : ProductEvent
}

sealed interface ProductEffect {
    data object NavigateBack : ProductEffect
    data object ShowAddToWatchlistBottomSheet : ProductEffect
    data class ShowToast(val message: String) : ProductEffect
}