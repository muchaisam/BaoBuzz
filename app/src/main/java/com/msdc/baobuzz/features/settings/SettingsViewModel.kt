package com.msdc.baobuzz.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msdc.baobuzz.core.data.LeagueData
import com.msdc.baobuzz.models.League
import com.msdc.baobuzz.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
@Inject
constructor(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    /** Available leagues for selection */
    val availableLeagues: List<League> = LeagueData.getPopularLeagues()

    init {
        observeUserPreferences()
    }

    /** Observes user preferences and updates UI state */
    private fun observeUserPreferences() {
        viewModelScope.launch {
            try {
                userPreferencesRepository.getPreferences().collect { preferences ->
                    val selectedLeagues =
                        preferences.selectedLeagueIds.mapNotNull { leagueId ->
                            LeagueData.getLeagueById(leagueId)
                        }

                    _uiState.value =
                        SettingsUiState.Loaded(
                            selectedLeagues = selectedLeagues,
                            notificationsEnabled = preferences.notificationsEnabled,
                            preferredLanguage = preferences.preferredLanguage,
                            isOnboardingCompleted = preferences.isOnboardingCompleted,
                            teamNotifications = preferences.teamNotifications
                        )
                }
            } catch (e: Exception) {
                _uiState.value =
                    SettingsUiState.Error(message = e.message ?: "Failed to load settings")
            }
        }
    }

    /** Add a league to user preferences */
    fun addLeague(league: League) {
        viewModelScope.launch {
            try {
                val currentPrefs = userPreferencesRepository.getPreferences().first()
                val updatedLeagueIds = (currentPrefs.selectedLeagueIds + league.id).distinct()

                userPreferencesRepository.savePreferences(
                    currentPrefs.copy(selectedLeagueIds = updatedLeagueIds)
                )
            } catch (e: Exception) {
                _uiState.value =
                    SettingsUiState.Error(message = "Failed to add league: ${e.message}")
            }
        }
    }

    /** Remove a league from user preferences */
    fun removeLeague(league: League) {
        viewModelScope.launch {
            try {
                val currentPrefs = userPreferencesRepository.getPreferences().first()
                val updatedLeagueIds = currentPrefs.selectedLeagueIds - league.id

                userPreferencesRepository.savePreferences(
                    currentPrefs.copy(selectedLeagueIds = updatedLeagueIds)
                )
            } catch (e: Exception) {
                _uiState.value =
                    SettingsUiState.Error(message = "Failed to remove league: ${e.message}")
            }
        }
    }

    /** Toggle global notifications */
    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            try {
                val currentPrefs = userPreferencesRepository.getPreferences().first()
                userPreferencesRepository.savePreferences(
                    currentPrefs.copy(notificationsEnabled = enabled)
                )
            } catch (e: Exception) {
                _uiState.value =
                    SettingsUiState.Error(
                        message = "Failed to update notifications: ${e.message}"
                    )
            }
        }
    }

    /** Toggle notifications for specific team */
    fun toggleTeamNotifications(teamId: Int, enabled: Boolean) {
        viewModelScope.launch {
            try {
                val currentPrefs = userPreferencesRepository.getPreferences().first()
                val updatedTeamNotifications = currentPrefs.teamNotifications.toMutableMap()
                updatedTeamNotifications[teamId] = enabled

                userPreferencesRepository.savePreferences(
                    currentPrefs.copy(teamNotifications = updatedTeamNotifications)
                )
            } catch (e: Exception) {
                _uiState.value =
                    SettingsUiState.Error(
                        message = "Failed to update team notifications: ${e.message}"
                    )
            }
        }
    }

    /** Change preferred language */
    fun changeLanguage(languageCode: String) {
        viewModelScope.launch {
            try {
                val currentPrefs = userPreferencesRepository.getPreferences().first()
                userPreferencesRepository.savePreferences(
                    currentPrefs.copy(preferredLanguage = languageCode)
                )
            } catch (e: Exception) {
                _uiState.value =
                    SettingsUiState.Error(message = "Failed to change language: ${e.message}")
            }
        }
    }

    /** Clear all user data and reset to onboarding */
    fun clearAllData() {
        viewModelScope.launch {
            try {
                userPreferencesRepository.clearAllPreferences()
                _uiState.value = SettingsUiState.DataCleared
            } catch (e: Exception) {
                _uiState.value =
                    SettingsUiState.Error(message = "Failed to clear data: ${e.message}")
            }
        }
    }

    /** Retry loading settings after an error */
    fun retry() {
        _uiState.value = SettingsUiState.Loading
        observeUserPreferences()
    }
}

/** Represents the different states of the Settings screen */
sealed class SettingsUiState {
    /** Loading state while fetching preferences */
    object Loading : SettingsUiState()

    /** Settings loaded successfully */
    data class Loaded(
        val selectedLeagues: List<League>,
        val notificationsEnabled: Boolean,
        val preferredLanguage: String,
        val isOnboardingCompleted: Boolean,
        val teamNotifications: Map<Int, Boolean>
    ) : SettingsUiState()

    /** Error state with retry capability */
    data class Error(val message: String) : SettingsUiState()

    /** Data has been cleared, user needs to restart onboarding */
    object DataCleared : SettingsUiState()
}

/** Supported languages for the app */
enum class SupportedLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    SPANISH("es", "Español"),
    FRENCH("fr", "Français"),
    GERMAN("de", "Deutsch"),
    ITALIAN("it", "Italiano")
}
