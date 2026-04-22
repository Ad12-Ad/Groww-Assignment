package com.example.growwassignment.ui.watchlist.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.growwassignment.ui.common.EmptyState
import com.example.growwassignment.ui.common.FundCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderDetailScreen(
    viewModel: FolderDetailViewModel,
    folderId: Int,
    folderName: String,
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (Int) -> Unit,
    onNavigateToExploreTab: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(folderId) {
        viewModel.onEvent(FolderDetailEvent.LoadFolder(folderId, folderName))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FolderDetailEffect.NavigateBack -> onNavigateBack()
                is FolderDetailEffect.NavigateToProduct -> onNavigateToProduct(effect.schemeCode)
                is FolderDetailEffect.NavigateToExploreTab -> onNavigateToExploreTab()
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.folderName,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(FolderDetailEvent.OnBackClicked) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.funds.isEmpty() -> {
                    EmptyState(
                        title = "No funds added yet.",
                        description = "Explore the market to save funds into this portfolio.",
                        actionText = "Explore Funds",
                        onActionClick = { viewModel.onEvent(FolderDetailEvent.OnExploreClicked) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.funds, key = { it.schemeCode }) { fund ->
                            FundCard(
                                schemeName = fund.schemeName,
                                nav = fund.latestNav,
                                onClick = { viewModel.onEvent(FolderDetailEvent.OnFundClicked(fund.schemeCode)) }
                            )
                        }
                    }
                }
            }
        }
    }
}