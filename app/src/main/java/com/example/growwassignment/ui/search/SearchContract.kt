package com.example.growwassignment.ui.search

import com.example.growwassignment.domain.model.FundSearch

data class SearchState(
    val query: String = "",
    val results: List<FundSearch> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isViewAllMode: Boolean = false,
    val viewAllCategoryTitle: String = ""
)

sealed interface SearchEvent {
    data class InitializeSearch(val initialQuery: String?, val categoryTitle: String?) : SearchEvent
    data class OnQueryChanged(val query: String) : SearchEvent
    data class OnFundClicked(val schemeCode: Int) : SearchEvent
    data object OnBackClicked : SearchEvent
    data object ClearSearch : SearchEvent
}

sealed interface SearchEffect {
    data class NavigateToProduct(val schemeCode: Int) : SearchEffect
    data object NavigateBack : SearchEffect
}