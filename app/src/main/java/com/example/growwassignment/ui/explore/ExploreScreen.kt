package com.example.growwassignment.ui.explore

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.growwassignment.domain.model.FundSearch
import com.example.growwassignment.ui.common.EmptyState
import com.example.growwassignment.ui.common.FundCard
import com.example.growwassignment.ui.common.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    onNavigateToProduct: (Int) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToViewAll: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ExploreEffect.NavigateToProduct -> onNavigateToProduct(effect.schemeCode)
                is ExploreEffect.NavigateToSearch -> onNavigateToSearch()
                is ExploreEffect.NavigateToViewAll -> onNavigateToViewAll(effect.categoryQuery)
                is ExploreEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "Hello, Investor \uD83D\uDC4B",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Find the best mutual funds for you",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.onEvent(ExploreEvent.OnSearchClicked) },
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Search Mutual Funds...",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.onEvent(ExploreEvent.RefreshCategories) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading && state.indexFunds.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                state.error != null && !state.isRefreshing && state.indexFunds.isEmpty() -> {
                    EmptyState(
                        title = "Error",
                        description = state.error!!,
                        modifier = Modifier.align(Alignment.Center),
                        actionText = "Retry",
                        onActionClick = { viewModel.onEvent(ExploreEvent.RefreshCategories) }
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        item {
                            CategorySection("Index Funds", "index", state.indexFunds, viewModel)
                        }
                        item {
                            CategorySection("Bluechip Funds", "bluechip", state.bluechipFunds, viewModel)
                        }
                        item {
                            CategorySection("Tax Saver (ELSS)", "tax", state.taxSaverFunds, viewModel)
                        }
                        item {
                            CategorySection("Large Cap Funds", "large cap", state.largeCapFunds, viewModel)
                        }
                        item { Spacer(modifier = Modifier.height(32.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategorySection(
    title: String,
    categoryQuery: String,
    funds: List<FundSearch>,
    viewModel: ExploreViewModel
) {
    Column {
        SectionHeader(
            title = title,
            onViewAllClick = { viewModel.onEvent(ExploreEvent.OnViewAllClicked(categoryQuery)) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        CategoryGrid(
            funds = funds,
            onItemClick = { schemeCode ->
                viewModel.onEvent(ExploreEvent.OnFundClicked(schemeCode))
            }
        )
    }
}

@Composable
private fun CategoryGrid(
    funds: List<FundSearch>,
    onItemClick: (Int) -> Unit
) {
    if (funds.isEmpty()) {
        Text(
            text = "No funds available at the moment.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val chunkedFunds = funds.take(4).chunked(2)

        chunkedFunds.forEach { rowFunds ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowFunds.forEach { fund ->
                    FundCard(
                        schemeName = fund.schemeName,
                        nav = null,
                        modifier = Modifier.weight(1f),
                        onClick = { onItemClick(fund.schemeCode) }
                    )
                }
                if (rowFunds.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}