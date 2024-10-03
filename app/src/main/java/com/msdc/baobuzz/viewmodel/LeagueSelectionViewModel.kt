package com.msdc.baobuzz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msdc.baobuzz.models.League
import com.msdc.baobuzz.models.Team
import com.msdc.baobuzz.models.UserPreferences
import com.msdc.baobuzz.repository.LeagueRepository
import com.msdc.baobuzz.repository.TeamRepository
import com.msdc.baobuzz.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeagueSelectionViewModel @Inject constructor(
    private val leagueRepository: LeagueRepository,
    private val teamRepository: TeamRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _leagues = MutableStateFlow<List<League>>(emptyList())
    val leagues: StateFlow<List<League>> = _leagues.asStateFlow()

    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    val teams: StateFlow<List<Team>> = _teams.asStateFlow()

    private val _selectedLeagues = MutableStateFlow<Set<Int>>(emptySet())
    val selectedLeagues: StateFlow<Set<Int>> = _selectedLeagues.asStateFlow()

    private val _selectedTeams = MutableStateFlow<Set<Int>>(emptySet())
    val selectedTeams: StateFlow<Set<Int>> = _selectedTeams.asStateFlow()

    private val _teamNotifications = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val teamNotifications: StateFlow<Map<Int, Boolean>> = _teamNotifications.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchResults = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                flowOf(_leagues.value)
            } else {
                flow { emit(leagueRepository.searchLeagues(query)) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, _leagues.value)

    init {
        loadLeagues()
    }
    private fun loadLeagues() {
        viewModelScope.launch {
            _leagues.value = leagueRepository.getLeagues()
            userPreferencesRepository.getPreferences().collect { prefs ->
                _selectedLeagues.value = prefs.selectedLeagueIds.toSet()
                _selectedTeams.value = prefs.selectedTeamIds.toSet()
                _teamNotifications.value = prefs.teamNotifications
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleLeagueSelection(leagueId: Int) {
        _selectedLeagues.value = _selectedLeagues.value.toMutableSet().apply {
            if (contains(leagueId)) remove(leagueId) else add(leagueId)
        }
        loadTeamsForSelectedLeagues()
    }

    fun toggleTeamSelection(teamId: Int) {
        _selectedTeams.value = _selectedTeams.value.toMutableSet().apply {
            if (contains(teamId)) remove(teamId) else add(teamId)
        }
    }

    fun toggleTeamNotification(teamId: Int) {
        _teamNotifications.value = _teamNotifications.value.toMutableMap().apply {
            this[teamId] = !(this[teamId] ?: false)
        }
    }

    private fun loadTeamsForSelectedLeagues() {
        viewModelScope.launch {
            val allTeams = mutableListOf<Team>()
            _selectedLeagues.value.forEach { leagueId ->
                val league = _leagues.value.find { it.id == leagueId }
                league?.let {
                    allTeams.addAll(teamRepository.getTeamsForLeague(leagueId, league.season))
                }
            }
            _teams.value = allTeams
        }
    }

    fun savePreferences() {
        viewModelScope.launch {
            userPreferencesRepository.savePreferences(
                UserPreferences(
                    selectedLeagueIds = _selectedLeagues.value.toList(),
                    selectedTeamIds = _selectedTeams.value.toList(),
                    teamNotifications = _teamNotifications.value
                )
            )
        }
    }

    fun loadTopFiveLeagues() {
        viewModelScope.launch {
            _leagues.value = leagueRepository.getTopFiveLeagues()
        }
    }
}
