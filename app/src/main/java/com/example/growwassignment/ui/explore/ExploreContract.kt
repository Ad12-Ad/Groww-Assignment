package com.example.growwassignment.ui.explore

import com.example.growwassignment.domain.model.FundSearch

data class ExploreState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val indexFunds: List<FundSearch> = emptyList(),
    val bluechipFunds: List<FundSearch> = emptyList(),
    val largeCapFunds: List<FundSearch> = emptyList(),
    val taxSaverFunds: List<FundSearch> = emptyList()
)

sealed interface ExploreEvent {
    data object LoadCategories : ExploreEvent
    data object RefreshCategories : ExploreEvent
    data class OnFundClicked(val schemeCode: Int) : ExploreEvent
    data object OnSearchClicked : ExploreEvent
    data class OnViewAllClicked(val categoryQuery: String) : ExploreEvent
}

sealed interface ExploreEffect {
    data class NavigateToProduct(val schemeCode: Int) : ExploreEffect
    data object NavigateToSearch : ExploreEffect
    data class NavigateToViewAll(val categoryQuery: String) : ExploreEffect
    data class ShowToast(val message: String) : ExploreEffect
}