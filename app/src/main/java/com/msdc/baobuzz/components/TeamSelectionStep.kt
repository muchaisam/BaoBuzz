package com.msdc.baobuzz.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.msdc.baobuzz.ui.EmptyTeamsMessage
import com.msdc.baobuzz.viewmodel.LeagueSelectionViewModel

@Composable
fun TeamSelectionStep(viewModel: LeagueSelectionViewModel) {
    val teams by viewModel.teams.collectAsState()
    val selectedTeams by viewModel.selectedTeams.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        when (uiState) {
            is LeagueSelectionViewModel.UiState.Loading -> {
                ShimmerTeamList()
            }
            is LeagueSelectionViewModel.UiState.Success -> {
                if (teams.isEmpty()) {
                    EmptyTeamsMessage()
                } else {
                    BubbleFlowRow {
                        teams.forEach { team ->
                            BubbleSelectionItem(
                                text = team.name,
                                icon = team.logo,
                                isSelected = selectedTeams.contains(team.id),
                                onClick = { viewModel.toggleTeamSelection(team.id) }
                            )
                        }
                    }
                }
            }
            is LeagueSelectionViewModel.UiState.Error -> {
                ErrorMessage(message = (uiState as LeagueSelectionViewModel.UiState.Error).message)
            }
        }
    }
}