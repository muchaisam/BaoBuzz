package com.msdc.baobuzz.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.msdc.baobuzz.ui.EmptyMessage
import com.msdc.baobuzz.viewmodel.LeagueSelectionViewModel

@Composable
fun LeagueSelectionStep(
    viewModel: LeagueSelectionViewModel,
    modifier: Modifier = Modifier
) {
    val leagues by viewModel.leagues.collectAsState()
    val selectedLeagues by viewModel.selectedLeagues.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    SelectionGrid(
        items = leagues,
        selectedIds = selectedLeagues,
        onItemClick = { league -> viewModel.toggleLeagueSelection(league.id) },
        itemContent = { league ->
            SelectionItem(
                text = league.name,
                imageUrl = league.logo,
                isSelected = selectedLeagues.contains(league.id)  // This uses selectedIds
            )
        },
        loadingContent = { ShimmerSelectionGrid() },
        emptyContent = { EmptyMessage("No leagues available") },
        uiState = uiState
    )
}
