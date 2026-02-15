package com.msdc.baobuzz.features.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msdc.baobuzz.core.data.LeagueData
import com.msdc.baobuzz.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.Stable
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel
@Inject
constructor(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        loadLeagues()
    }

    private fun loadLeagues() {
        _uiState.value =
            _uiState.value.copy(
                availableLeagues = LeagueData.getOnboardingLeagues(),
                isLoading = false
            )
    }

    fun toggleLeagueSelection(leagueId: Int) {
        val currentSelected = _uiState.value.selectedLeagueIds.toMutableSet()
        if (currentSelected.contains(leagueId)) {
            currentSelected.remove(leagueId)
        } else {
            currentSelected.add(leagueId)
        }

        _uiState.value =
            _uiState.value.copy(
                selectedLeagueIds = currentSelected,
                canContinue = currentSelected.isNotEmpty()
            )
    }

    fun completeOnboarding(onSuccess: () -> Unit) {
        val selectedIds = _uiState.value.selectedLeagueIds.toList()

        if (selectedIds.isEmpty()) {
            _uiState.value =
                _uiState.value.copy(
                    errorMessage = "Please select at least one league to continue"
                )
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

                userPreferencesRepository.markOnboardingComplete(selectedIds)

                _uiState.value = _uiState.value.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to save preferences. Please try again."
                    )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    // Legacy methods for backward compatibility
    fun completeOnboarding(selectedLeagues: List<Int>, favoriteTeams: List<Int> = emptyList()) {
        viewModelScope.launch { userPreferencesRepository.markOnboardingComplete(selectedLeagues) }
    }

    fun skipOnboarding() {
        viewModelScope.launch { userPreferencesRepository.markOnboardingComplete(emptyList()) }
    }
}

@Stable
data class OnboardingUiState(
    val availableLeagues: List<LeagueData.OnboardingLeague> = emptyList(),
    val selectedLeagueIds: Set<Int> = emptySet(),
    val isLoading: Boolean = true,
    val canContinue: Boolean = false,
    val errorMessage: String? = null
)
