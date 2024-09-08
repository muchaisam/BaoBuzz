package com.example.baobuzz.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.baobuzz.R
import com.example.baobuzz.api.ApiClient
import com.example.baobuzz.daos.AppDatabase
import com.example.baobuzz.models.LeagueStandings
import com.example.baobuzz.models.StandingsUiState
import com.example.baobuzz.models.StandingsViewModel
import com.example.baobuzz.models.TeamStanding
import com.example.baobuzz.repository.FootballRepository
import com.example.baobuzz.repository.StandingsRepository
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import coil.compose.AsyncImage
import com.example.baobuzz.models.LeagueStanding


class StatisticsFragment : Fragment() {
    private lateinit var viewModel: StandingsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val repository = StandingsRepository(ApiClient.footballApi,
                AppDatabase.getInstance(requireContext()))
        val factory = StandingsViewModel.Factory(repository)
        viewModel = ViewModelProvider(this, factory)[StandingsViewModel::class.java]

        return ComposeView(requireContext()).apply {
            setContent {
                val darkTheme = isSystemInDarkTheme()
                MaterialTheme(
                    colors = if (darkTheme) darkColors() else lightColors()
                ) {
                    StandingsScreen(viewModel)
                }
            }
        }
    }

    @Composable
    fun StandingsScreen(viewModel: StandingsViewModel) {
        val uiState by viewModel.uiState.collectAsState()
        val selectedLeague by viewModel.selectedLeague.collectAsState()

        Column {
            when (val state = uiState) {
                is StandingsUiState.Loading -> LoadingScreen()
                is StandingsUiState.Success -> {
                    LeagueSelector(
                        leagues = state.standings.keys.toList(),
                        selectedLeague = selectedLeague,
                        onLeagueSelected = { viewModel.selectLeague(it) }
                    )
                    StandingsList(standings = state.standings[selectedLeague])
                }
                is StandingsUiState.Error -> ErrorScreen(state.message) { viewModel.retry() }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun LeagueSelector(
        leagues: List<Int>,
        selectedLeague: Int,
        onLeagueSelected: (Int) -> Unit
    ) {
        val leagueNames = mapOf(
            39 to "Premier League",
            140 to "La Liga",
            61 to "Ligue 1",
            78 to "Bundesliga",
            135 to "Serie A"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            leagues.forEach { leagueId ->
                Chip(
                    onClick = { onLeagueSelected(leagueId) },
                    colors = ChipDefaults.chipColors(
                        backgroundColor = if (leagueId == selectedLeague) MaterialTheme.colors.primary else MaterialTheme.colors.surface
                    )
                ) {
                    Text(leagueNames[leagueId] ?: "Unknown")
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }

    @Composable
    fun LoadingScreen() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    @Composable
    fun ErrorScreen(message: String, onRetry: () -> Unit) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = message, style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }

    @Composable
    fun StandingsList(standings: LeagueStandings?) {
        LazyColumn {
            item {
                standings?.league?.let { league ->
                    LeagueHeader(league)
                }
            }

            standings?.league?.standings?.firstOrNull()?.let { teamStandings ->
                items(teamStandings) { standing ->
                    StandingItem(standing)
                }
            }
        }
    }

    @Composable
    fun LeagueHeader(league: LeagueStanding) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = league.logo,
                contentDescription = "League logo",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = league.name,
                style = MaterialTheme.typography.h5
            )
        }
    }

    @Composable
    fun StandingItem(standing: TeamStanding) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            elevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${standing.rank}.",
                    modifier = Modifier.width(30.dp),
                    style = MaterialTheme.typography.body2
                )
                AsyncImage(
                    model = standing.team.logo,
                    contentDescription = "Team logo",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = firstWordAbbreviation(standing.team.name),
                    modifier = Modifier.width(100.dp),
                    style = MaterialTheme.typography.body2
                )
                Spacer(modifier = Modifier.weight(1f))
                StandingStats(standing)
            }
        }
    }

    @Composable
    fun StandingStats(standing: TeamStanding) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${standing.all.played}",
                style = MaterialTheme.typography.body2,
                modifier = Modifier.width(25.dp)
            )
            Text(
                text = "${standing.all.win}",
                style = MaterialTheme.typography.body2,
                modifier = Modifier.width(25.dp)
            )
            Text(
                text = "${standing.all.draw}",
                style = MaterialTheme.typography.body2,
                modifier = Modifier.width(25.dp)
            )
            Text(
                text = "${standing.all.lose}",
                style = MaterialTheme.typography.body2,
                modifier = Modifier.width(25.dp)
            )
            Text(
                text = "GD: ${standing.goalsDiff}",
                style = MaterialTheme.typography.body2,
                modifier = Modifier.width(50.dp)
            )
            Text(
                text = "${standing.points}",
                style = MaterialTheme.typography.body2,
                modifier = Modifier.width(30.dp)
            )
        }
    }

    fun firstWordAbbreviation(name: String): String {
        return name.split(" ").first().take(3).uppercase()
    }

}