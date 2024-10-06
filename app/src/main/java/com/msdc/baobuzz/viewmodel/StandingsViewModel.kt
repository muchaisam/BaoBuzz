package com.msdc.baobuzz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.msdc.baobuzz.models.LeagueStandings
import com.msdc.baobuzz.models.PlayerStats
import com.msdc.baobuzz.repository.StandingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class StandingsUiState {
    object Loading : StandingsUiState()
    data class Success(
        val standings: Map<Int, LeagueStandings?>,
        val topScorers: Map<Int, List<PlayerStats>>,
        val topAssisters: Map<Int, List<PlayerStats>>
    ) : StandingsUiState()
    data class Error(val message: String) : StandingsUiState()
}

class StandingsViewModel(private val repository: StandingsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<StandingsUiState>(StandingsUiState.Loading)
    val uiState: StateFlow<StandingsUiState> = _uiState

    private val _selectedLeague = MutableStateFlow<Int>(39) // Default to Premier League
    val selectedLeague: StateFlow<Int> = _selectedLeague

    private val topFiveLeagues = listOf(39, 140, 61, 78, 135) // Premier League, La Liga, Ligue 1, Bundesliga, Serie A
    private val currentSeason = 2024

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                val standings = mutableMapOf<Int, LeagueStandings?>()
                val topScorers = mutableMapOf<Int, List<PlayerStats>>()
                val topAssisters = mutableMapOf<Int, List<PlayerStats>>()

                topFiveLeagues.forEach { leagueId ->
                    launch {
                        standings[leagueId] = repository.getStandings(leagueId, currentSeason)
                    }
                    launch {
                        topScorers[leagueId] = repository.getTopScorers(leagueId, currentSeason)
                    }
                    launch {
                        topAssisters[leagueId] = repository.getTopAssisters(leagueId, currentSeason)
                    }
                }

                _uiState.value = StandingsUiState.Success(standings, topScorers, topAssisters)
            } catch (e: Exception) {
                _uiState.value = StandingsUiState.Error("Failed to load data: ${e.message}")
            }
        }
    }

    fun selectLeague(leagueId: Int) {
        _selectedLeague.value = leagueId
    }

    fun retry() {
        _uiState.value = StandingsUiState.Loading
        loadData()
    }

    fun getLeagueInfo(): Map<Int, Pair<String, String>> {
        return mapOf(
            39 to Pair("Premier League", "https://media.api-sports.io/football/leagues/39.png"),
            140 to Pair("La Liga", "https://media.api-sports.io/football/leagues/140.png"),
            61 to Pair("Ligue 1", "https://media.api-sports.io/football/leagues/61.png"),
            78 to Pair("Bundesliga", "https://media.api-sports.io/football/leagues/78.png"),
            135 to Pair("Serie A", "https://media.api-sports.io/football/leagues/135.png")
        )
    }

    class Factory(private val repository: StandingsRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StandingsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return StandingsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}