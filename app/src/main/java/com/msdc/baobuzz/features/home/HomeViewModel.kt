package com.msdc.baobuzz.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msdc.baobuzz.core.api.FootballRepository
import com.msdc.baobuzz.core.data.LeagueData
import com.msdc.baobuzz.core.models.LeagueInsight
import com.msdc.baobuzz.core.models.LeagueStanding
import com.msdc.baobuzz.core.models.LiveMatch
import com.msdc.baobuzz.core.models.PlayerStat
import com.msdc.baobuzz.core.models.RecentResult
import com.msdc.baobuzz.core.models.TransferDetails
import com.msdc.baobuzz.core.models.UpcomingFixture
import com.msdc.baobuzz.models.League
import com.msdc.baobuzz.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val footballRepository: FootballRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    /** User's selected leagues with metadata for enhanced UI */
    val selectedLeaguesWithData: StateFlow<List<League>> =
        userPreferencesRepository
            .getPreferences()
            .map { preferences ->
                preferences.selectedLeagueIds.mapNotNull { leagueId ->
                    LeagueData.getLeagueById(leagueId)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    /** Indicates if user has completed onboarding and selected leagues */
    val hasSelectedLeagues: StateFlow<Boolean> =
        userPreferencesRepository
            .getPreferences()
            .map { preferences ->
                preferences.isOnboardingCompleted &&
                        preferences.selectedLeagueIds.isNotEmpty()
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
            )

    init {
        loadHomeData()
        // Observe user preferences changes and reload data when leagues change
        observeUserPreferencesChanges()
    }

    /** Observes user preferences and reloads data when selected leagues change */
    private fun observeUserPreferencesChanges() {
        viewModelScope.launch {
            userPreferencesRepository
                .getPreferences()
                .map { it.selectedLeagueIds }
                .distinctUntilChanged()
                .collect { selectedLeagueIds ->
                    if (selectedLeagueIds.isNotEmpty() && _uiState.value !is HomeUiState.Loading
                    ) {
                        loadHomeData()
                    }
                }
        }
    }

    /** Loads home screen data based on user's selected leagues */
    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                _uiState.value = HomeUiState.Loading

                // Get user's selected leagues
                val userPreferences = userPreferencesRepository.getPreferences().first()
                val selectedLeagueIds = userPreferences.selectedLeagueIds

                if (!userPreferences.isOnboardingCompleted) {
                    _uiState.value = HomeUiState.OnboardingRequired
                    return@launch
                }

                if (selectedLeagueIds.isEmpty()) {
                    _uiState.value = HomeUiState.NoLeaguesSelected
                    return@launch
                }

                // Add a small delay to prevent rapid API calls during preference changes
                delay(300)

                // Load all data in parallel
                coroutineScope {
                    val liveMatchesDeferred = async { footballRepository.getLiveMatches(selectedLeagueIds) }
                    val recentTransfersDeferred = async { footballRepository.getRecentTransfers(selectedLeagueIds) }
                    val leagueStandingsDeferred = async {
                        selectedLeagueIds.mapNotNull { leagueId ->
                            try {
                                footballRepository.getLeagueStandings(leagueId)
                            } catch (e: Exception) {
                                null
                            }
                        }
                    }
                    val upcomingFixturesDeferred = async { footballRepository.getUpcomingFixtures(selectedLeagueIds, 8) }
                    val recentResultsDeferred = async { footballRepository.getRecentResults(selectedLeagueIds, 6) }
                    val leagueInsightsDeferred = async { footballRepository.getLeagueInsights(selectedLeagueIds) }
                    val topScorersDeferred = async {
                        selectedLeagueIds.flatMap { leagueId ->
                            try {
                                footballRepository.getTopScorers(leagueId).take(3)
                            } catch (e: Exception) {
                                emptyList()
                            }
                        }
                    }

                    _uiState.value =
                        HomeUiState.Success(
                            liveMatches = liveMatchesDeferred.await(),
                            recentTransfers = recentTransfersDeferred.await(),
                            leagueStandings = leagueStandingsDeferred.await(),
                            selectedLeagues =
                                selectedLeagueIds.mapNotNull { leagueId ->
                                    LeagueData.getLeagueById(leagueId)
                                },
                            upcomingFixtures = upcomingFixturesDeferred.await(),
                            recentResults = recentResultsDeferred.await(),
                            leagueInsights = leagueInsightsDeferred.await(),
                            topScorers = topScorersDeferred.await()
                        )
                }
            } catch (e: Exception) {
                _uiState.value =
                    HomeUiState.Error(
                        message = e.message ?: "Failed to load football data",
                        canRetry = true
                    )
            }
        }
    }

    /** Retry loading data after an error */
    fun retry() {
        loadHomeData()
    }

    /** Refresh data manually */
    fun refreshData() {
        loadHomeData()
    }

    // Backward compatibility
    fun loadData() {
        loadHomeData()
    }
}

/** Represents the different states of the Home screen UI */
sealed class HomeUiState {
    /** Loading state while fetching data */
    object Loading : HomeUiState()

    /** User needs to complete onboarding */
    object OnboardingRequired : HomeUiState()

    /** User completed onboarding but hasn't selected any leagues */
    object NoLeaguesSelected : HomeUiState()

    /** Successful state with football data */
    data class Success(
        val liveMatches: List<LiveMatch>,
        val recentTransfers: List<TransferDetails>,
        val leagueStandings: List<LeagueStanding>,
        val selectedLeagues: List<League>,
        val upcomingFixtures: List<UpcomingFixture> = emptyList(),
        val recentResults: List<RecentResult> = emptyList(),
        val leagueInsights: List<LeagueInsight> = emptyList(),
        val topScorers: List<PlayerStat> = emptyList()
    ) : HomeUiState()

    /** Error state with retry capability */
    data class Error(val message: String, val canRetry: Boolean = true) : HomeUiState()
}
