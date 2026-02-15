package com.msdc.baobuzz.features.leagues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msdc.baobuzz.core.api.FootballRepository
import com.msdc.baobuzz.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaguesViewModel
@Inject
constructor(
    private val footballRepository: FootballRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LeaguesUiState>(LeaguesUiState.Loading)
    val uiState: StateFlow<LeaguesUiState> = _uiState.asStateFlow()

    init {
        loadLeaguesData()
    }

    private fun loadLeaguesData() {
        viewModelScope.launch {
            try {
                _uiState.value = LeaguesUiState.Loading

                // Get user's selected leagues
                val userPreferences = userPreferencesRepository.getPreferences().first()
                val selectedLeagues = userPreferences.selectedLeagueIds

                if (selectedLeagues.isEmpty()) {
                    _uiState.value =
                        LeaguesUiState.Success(
                            standings = emptyList(),
                            fixtures = emptyList(),
                            transfers = emptyList()
                        )
                    return@launch
                }

                // Load standings for all selected leagues
                val standings =
                    selectedLeagues.mapNotNull { leagueId ->
                        footballRepository.getLeagueStandings(leagueId)
                    }

                // Get upcoming fixtures for next 7 days
                val currentDate = java.time.LocalDate.now()
                val nextWeek = currentDate.plusDays(7)
                val fixtures =
                    selectedLeagues.flatMap { leagueId ->
                        footballRepository.getFixtures(
                            leagueId = leagueId,
                            from = currentDate.toString(),
                            to = nextWeek.toString()
                        )
                    }

                // Get recent transfers
                val transfers = footballRepository.getRecentTransfers(selectedLeagues)

                _uiState.value =
                    LeaguesUiState.Success(
                        standings = standings,
                        fixtures = fixtures,
                        transfers = transfers
                    )
            } catch (e: Exception) {
                _uiState.value =
                    LeaguesUiState.Error(message = e.message ?: "Unknown error occurred")
            }
        }
    }

    fun retry() {
        loadLeaguesData()
    }
}
