package com.msdc.baobuzz.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.msdc.baobuzz.viewmodel.LeagueSelectionViewModel

@Composable
fun LeagueSelectionStep(viewModel: LeagueSelectionViewModel) {
    val leagues by viewModel.leagues.collectAsState()
    val selectedLeagues by viewModel.selectedLeagues.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        when (uiState) {
            is LeagueSelectionViewModel.UiState.Loading -> {
                ShimmerLeagueList()
            }
            is LeagueSelectionViewModel.UiState.Success -> {
                BubbleFlowRow {
                    leagues.forEach { league ->
                        BubbleSelectionItem(
                            text = league.name,
                            icon = league.logo,
                            isSelected = selectedLeagues.contains(league.id),
                            onClick = { viewModel.toggleLeagueSelection(league.id) }
                        )
                    }
                }
            }
            is LeagueSelectionViewModel.UiState.Error -> {
                ErrorMessage(message = (uiState as LeagueSelectionViewModel.UiState.Error).message)
            }
        }
    }
}

