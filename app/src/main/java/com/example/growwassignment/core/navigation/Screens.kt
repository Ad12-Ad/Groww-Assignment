package com.example.growwassignment.core.navigation

sealed class Screen(val route: String) {
    data object Explore : Screen("explore_screen")
    data object WatchlistTab : Screen("watchlist_tab_screen")

    data object Product : Screen("product_screen/{schemeCode}") {
        fun createRoute(schemeCode: Int) = "product_screen/$schemeCode"
    }

    data object Search : Screen("search_screen?initialQuery={initialQuery}&categoryTitle={categoryTitle}") {
        fun createRoute(initialQuery: String? = null, categoryTitle: String? = null): String {
            return if (initialQuery != null && categoryTitle != null) {
                "search_screen?initialQuery=$initialQuery&categoryTitle=$categoryTitle"
            } else {
                "search_screen"
            }
        }
    }

    data object FolderDetail : Screen("folder_detail_screen/{folderId}/{folderName}") {
        fun createRoute(folderId: Int, folderName: String) = "folder_detail_screen/$folderId/$folderName"
    }
}