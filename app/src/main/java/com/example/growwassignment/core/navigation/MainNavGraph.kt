package com.example.growwassignment.core.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.growwassignment.ui.explore.ExploreScreen
import com.example.growwassignment.ui.explore.ExploreViewModel
import com.example.growwassignment.ui.product.ProductScreen
import com.example.growwassignment.ui.product.ProductViewModel
import com.example.growwassignment.ui.product.watchlist.WatchlistBottomSheet
import com.example.growwassignment.ui.product.watchlist.WatchlistSheetViewModel
import com.example.growwassignment.ui.search.SearchScreen
import com.example.growwassignment.ui.search.SearchViewModel
import com.example.growwassignment.ui.watchlist.WatchlistScreen
import com.example.growwassignment.ui.watchlist.WatchlistViewModel
import com.example.growwassignment.ui.watchlist.detail.FolderDetailScreen
import com.example.growwassignment.ui.watchlist.detail.FolderDetailViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route == Screen.Explore.route ||
            currentDestination?.route == Screen.WatchlistTab.route

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                Surface(
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    shadowElevation = 16.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp
                    ) {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
                            label = { Text("Explore", fontWeight = FontWeight.Bold) },
                            selected = currentDestination?.hierarchy?.any { it.route == Screen.Explore.route } == true,
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary
                            ),
                            onClick = {
                                navController.navigate(Screen.Explore.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Watchlist") },
                            label = { Text("Watchlist", fontWeight = FontWeight.Bold) },
                            selected = currentDestination?.hierarchy?.any { it.route == Screen.WatchlistTab.route } == true,
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary
                            ),
                            onClick = {
                                navController.navigate(Screen.WatchlistTab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Explore.route,
            modifier = Modifier
        ) {
            composable(Screen.Explore.route) {
                val viewModel: ExploreViewModel = hiltViewModel()
                ExploreScreen(
                    viewModel = viewModel,
                    onNavigateToProduct = { schemeCode -> navController.navigate(
                        Screen.Product.createRoute(
                            schemeCode
                        )
                    ) },
                    onNavigateToSearch = { navController.navigate(Screen.Search.createRoute()) },
                    onNavigateToViewAll = { query ->
                        val title = when(query) {
                            "index" -> "Index Funds"
                            "bluechip" -> "Bluechip Funds"
                            "tax" -> "Tax Saver Funds"
                            "large cap" -> "Large Cap Funds"
                            else -> "Funds"
                        }
                        navController.navigate(
                            Screen.Search.createRoute(
                                initialQuery = query,
                                categoryTitle = title
                            )
                        )
                    }
                )
            }

            composable(Screen.WatchlistTab.route) {
                val viewModel: WatchlistViewModel = hiltViewModel()
                WatchlistScreen(
                    viewModel = viewModel,
                    onNavigateToFolderDetail = { id, name ->
                        navController.navigate(Screen.FolderDetail.createRoute(id, name))
                    }
                )
            }

            composable(
                route = Screen.Search.route,
                arguments = listOf(
                    navArgument("initialQuery") { type = NavType.StringType; nullable = true; defaultValue = null },
                    navArgument("categoryTitle") { type = NavType.StringType; nullable = true; defaultValue = null }
                )
            ) { backStackEntry ->
                val initialQuery = backStackEntry.arguments?.getString("initialQuery")
                val categoryTitle = backStackEntry.arguments?.getString("categoryTitle")
                val viewModel: SearchViewModel = hiltViewModel()

                SearchScreen(
                    viewModel = viewModel,
                    initialQuery = initialQuery,
                    categoryTitle = categoryTitle,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProduct = { schemeCode -> navController.navigate(
                        Screen.Product.createRoute(
                            schemeCode
                        )
                    ) }
                )
            }

            composable(
                route = Screen.Product.route,
                arguments = listOf(navArgument("schemeCode") { type = NavType.IntType })
            ) { backStackEntry ->
                val schemeCode = backStackEntry.arguments?.getInt("schemeCode") ?: return@composable
                val productViewModel: ProductViewModel = hiltViewModel()
                val productState by productViewModel.state.collectAsStateWithLifecycle()

                var showWatchlistSheet by remember { mutableStateOf(false) }

                ProductScreen(
                    viewModel = productViewModel,
                    schemeCode = schemeCode,
                    onNavigateBack = { navController.popBackStack() },
                    onOpenWatchlistSheet = { showWatchlistSheet = true }
                )

                if (showWatchlistSheet && productState.fundDetails != null) {
                    val watchlistSheetViewModel: WatchlistSheetViewModel = hiltViewModel()
                    val details = productState.fundDetails!!
                    val latestNav = details.navHistory.firstOrNull()?.nav?.toString() ?: "0.0"

                    WatchlistBottomSheet(
                        viewModel = watchlistSheetViewModel,
                        schemeCode = details.schemeCode,
                        schemeName = details.schemeName,
                        latestNav = latestNav,
                        onDismiss = { showWatchlistSheet = false }
                    )
                }
            }

            composable(
                route = Screen.FolderDetail.route,
                arguments = listOf(
                    navArgument("folderId") { type = NavType.IntType },
                    navArgument("folderName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val folderId = backStackEntry.arguments?.getInt("folderId") ?: return@composable
                val folderName = backStackEntry.arguments?.getString("folderName") ?: return@composable
                val viewModel: FolderDetailViewModel = hiltViewModel()

                FolderDetailScreen(
                    viewModel = viewModel,
                    folderId = folderId,
                    folderName = folderName,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProduct = { schemeCode -> navController.navigate(
                        Screen.Product.createRoute(
                            schemeCode
                        )
                    ) },
                    onNavigateToExploreTab = {
                        navController.navigate(Screen.Explore.route) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                        }
                    }
                )
            }
        }
    }
}