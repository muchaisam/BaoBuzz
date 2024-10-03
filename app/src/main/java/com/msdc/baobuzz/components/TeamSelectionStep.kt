package com.msdc.baobuzz.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.msdc.baobuzz.ui.EmptyMessage
import com.msdc.baobuzz.viewmodel.LeagueSelectionViewModel

@Composable
fun TeamSelectionStep(
    viewModel: LeagueSelectionViewModel,
    modifier: Modifier = Modifier
) {
    val teams by viewModel.teams.collectAsState()
    val selectedTeams by viewModel.selectedTeams.collectAsState()
    val selectedLeagues by viewModel.selectedLeagues.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = selectedLeagues.isNotEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            SelectedLeaguesSummary(
                selectedLeagueCount = selectedLeagues.size,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        SelectionGrid(
            items = teams,
            selectedIds = selectedTeams,  // This is now properly used
            onItemClick = { team -> viewModel.toggleTeamSelection(team.id) },
            itemContent = { team ->
                SelectionItem(
                    text = team.name,
                    imageUrl = team.logo,
                    isSelected = selectedTeams.contains(team.id)  // This uses selectedIds
                )
            },
            loadingContent = { ShimmerSelectionGrid() },
            emptyContent = {
                EmptyMessage(
                    message = if (selectedLeagues.isEmpty())
                        "Select leagues first to see teams"
                    else
                        "No teams found for selected leagues"
                )
            },
            uiState = uiState
        )
    }
}
