package com.example.baobuzz.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.baobuzz.repository.StandingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class StandingsUiState {
    object Loading : StandingsUiState()
    data class Success(val standings: Map<Int, LeagueStandings?>) : StandingsUiState()
    data class Error(val message: String) : StandingsUiState()
}

class StandingsViewModel(private val repository: StandingsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<StandingsUiState>(StandingsUiState.Loading)
    val uiState: StateFlow<StandingsUiState> = _uiState

    private val topFiveLeagues = listOf(39, 140, 61, 78, 135) // Premier League, La Liga, Ligue 1, Bundesliga, Serie A
    private val currentSeason = 2023 // Update this value for the current season

    init {
        loadStandings()
    }

    private fun loadStandings() {
        viewModelScope.launch {
            try {
                val standings = mutableMapOf<Int, LeagueStandings?>()
                topFiveLeagues.forEach { leagueId ->
                    val leagueStandings = repository.getStandings(leagueId, currentSeason)
                    standings[leagueId] = leagueStandings
                }
                _uiState.value = StandingsUiState.Success(standings)
            } catch (e: Exception) {
                _uiState.value = StandingsUiState.Error("Failed to load standings: ${e.message}")
            }
        }
    }

    fun retry() {
        _uiState.value = StandingsUiState.Loading
        loadStandings()
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