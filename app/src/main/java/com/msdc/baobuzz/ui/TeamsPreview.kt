package com.msdc.baobuzz.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.msdc.baobuzz.components.coaches.LoadingGrid
import com.msdc.baobuzz.components.standings.ErrorScreen
import com.msdc.baobuzz.components.stats.AnimatedSliderLeagueHeader
import com.msdc.baobuzz.components.stats.MiniStandingsList
import com.msdc.baobuzz.components.stats.TopAssistersList
import com.msdc.baobuzz.components.stats.TopScorersList
import com.msdc.baobuzz.viewmodel.StandingsUiState
import com.msdc.baobuzz.viewmodel.StandingsViewModel

@Composable
fun TeamsPreview(
    viewModel: StandingsViewModel,
    onNavigateToFullStandings: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedLeague by viewModel.selectedLeague.collectAsState()

    LazyColumn(modifier = Modifier.height(950.dp)){
        when (val state = uiState) {
            is StandingsUiState.Loading -> {
                item { LoadingGrid() }
            }
            is StandingsUiState.Success -> {
                item {
                    AnimatedSliderLeagueHeader(
                        leagues = state.standings.keys.toList(),
                        selectedLeague = selectedLeague,
                        onLeagueSelected = { viewModel.selectLeague(it) },
                        leagueInfo = viewModel.getLeagueInfo()
                    )
                }
                item {
                    MiniStandingsList(
                        standings = state.standings[selectedLeague],
                        onViewFullStandings = {
                            onNavigateToFullStandings(selectedLeague)
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    TopScorersList(
                        players = state.topScorers[selectedLeague]
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    TopAssistersList(
                        players = state.topAssisters[selectedLeague]
                    )
                }
            }
            is StandingsUiState.Error -> {
                item { ErrorScreen(state.message) { viewModel.retry() } }
            }
        }
    }
}