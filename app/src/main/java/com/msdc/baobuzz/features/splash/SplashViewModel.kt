package com.msdc.baobuzz.features.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msdc.baobuzz.core.api.ApiRequestTracker
import com.msdc.baobuzz.core.preferences.OnboardingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val onboardingPreferences: OnboardingPreferences,
    private val apiRequestTracker: ApiRequestTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        loadApiUsageInfo()
    }

    fun checkOnboardingStatus() {
        viewModelScope.launch {
            onboardingPreferences.isOnboardingCompleted.first().let { isCompleted ->
                _uiState.update { currentState ->
                    currentState.copy(
                        navigationDestination = if (isCompleted) {
                            SplashNavigationDestination.MainApp
                        } else {
                            SplashNavigationDestination.Onboarding
                        }
                    )
                }
            }
        }
    }

    private fun loadApiUsageInfo() {
        viewModelScope.launch {
            try {
                val remaining = apiRequestTracker.getRemainingRequests()
                _uiState.update { currentState ->
                    currentState.copy(remainingApiRequests = remaining)
                }
            } catch (e: Exception) {
                // Silently handle error, not critical for splash screen
            }
        }
    }
}

data class SplashUiState(
    val remainingApiRequests: Int? = null,
    val navigationDestination: SplashNavigationDestination? = null
)

enum class SplashNavigationDestination {
    Onboarding,
    MainApp
}
