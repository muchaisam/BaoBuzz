package com.msdc.baobuzz.features.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msdc.baobuzz.core.api.FootballRepository
import com.msdc.baobuzz.core.models.PlayerStat
import com.msdc.baobuzz.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel
@Inject
constructor(
    private val footballRepository: FootballRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<StatsUiState>(StatsUiState.Loading)
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStatsData()
    }

    private fun loadStatsData() {
        viewModelScope.launch {
            try {
                _uiState.value = StatsUiState.Loading

                // Get user's selected leagues
                val userPreferences = userPreferencesRepository.getPreferences().first()
                val selectedLeagues = userPreferences.selectedLeagueIds

                if (selectedLeagues.isEmpty()) {
                    _uiState.value =
                        StatsUiState.Success(
                            topScorers = emptyList(),
                            topAssisters = emptyList()
                        )
                    return@launch
                }

                // Load top scorers and assisters for all selected leagues
                val allTopScorers = mutableListOf<PlayerStat>()
                val allTopAssisters = mutableListOf<PlayerStat>()

                selectedLeagues.forEach { leagueId ->
                    val scorers = footballRepository.getTopScorers(leagueId)
                    val assisters = footballRepository.getTopAssisters(leagueId)

                    allTopScorers.addAll(scorers)
                    allTopAssisters.addAll(assisters)
                }

                _uiState.value =
                    StatsUiState.Success(
                        topScorers = allTopScorers.sortedByDescending { it.goals }.take(20),
                        topAssisters =
                            allTopAssisters.sortedByDescending { it.assists }.take(20)
                    )
            } catch (e: Exception) {
                _uiState.value = StatsUiState.Error(message = e.message ?: "Unknown error occurred")
            }
        }
    }

    fun retry() {
        loadStatsData()
    }
}
