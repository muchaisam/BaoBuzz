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

        when (val state = uiState) {
            is StandingsUiState.Loading -> LoadingScreen()
            is StandingsUiState.Success -> StandingsList(state.standings)
            is StandingsUiState.Error -> ErrorScreen(state.message) { viewModel.retry() }
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
    fun StandingsList(standings: Map<Int, LeagueStandings?>) {
        LazyColumn {
            standings.forEach { (leagueId, leagueStandings) ->
                item {
                    Text(
                        text = leagueStandings?.league?.name ?: "Unknown League",
                        style = MaterialTheme.typography.h5,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                leagueStandings?.league?.standings?.firstOrNull()?.let { teamStandings ->
                    items(teamStandings) { standing ->
                        StandingItem(standing)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    @Composable
    fun StandingItem(standing: TeamStanding) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "${standing.rank}.", modifier = Modifier.width(30.dp))
            Text(text = standing.team.name, modifier = Modifier.weight(1f))
            Text(text = "${standing.points} pts", modifier = Modifier.width(50.dp))
        }
    }

}