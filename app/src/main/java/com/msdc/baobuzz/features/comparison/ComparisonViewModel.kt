package com.msdc.baobuzz.features.comparison

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msdc.baobuzz.core.api.HistoricalRepository
import com.msdc.baobuzz.core.models.SeasonComparison
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ComparisonViewModel - Powers the Season Comparison Feature 🔄
 *
 * Features:
 * - Side-by-side season comparison
 * - Statistical analysis
 * - Visual charts and graphs
 * - League selection
 * - Season selection with validation
 */
@HiltViewModel
class ComparisonViewModel @Inject constructor(
    private val historicalRepository: HistoricalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ComparisonUiState>(ComparisonUiState.Initial)
    val uiState: StateFlow<ComparisonUiState> = _uiState.asStateFlow()

    private var availableSeasons: List<String> = emptyList()
    private val availableLeagues = HistoricalRepository.SUPPORTED_LEAGUES

    init {
        loadAvailableSeasons()
    }

    /**
     * Load available seasons for comparison
     */
    private fun loadAvailableSeasons() {
        viewModelScope.launch {
            try {
                // For now, use the default league (Premier League)
                availableSeasons = historicalRepository.getAvailableSeasons("en.1")
                _uiState.value = ComparisonUiState.Initial
            } catch (e: Exception) {
                _uiState.value = ComparisonUiState.Error(e.message ?: "Failed to load seasons")
            }
        }
    }

    /**
     * Get available leagues
     */
    fun getAvailableLeagues(): Map<String, String> {
        return availableLeagues
    }

    /**
     * Get available seasons
     */
    fun getAvailableSeasons(): List<String> {
        return availableSeasons
    }

    /**
     * Compare two seasons
     */
    fun compareSeasons(league: String, season1: String, season2: String) {
        if (season1 == season2) {
            _uiState.value = ComparisonUiState.Error("Please select different seasons to compare")
            return
        }

        viewModelScope.launch {
            _uiState.value = ComparisonUiState.Loading

            try {
                val comparison = historicalRepository.compareSeasons(league, season1, season2)
                _uiState.value = ComparisonUiState.Success(comparison)
            } catch (e: Exception) {
                _uiState.value = ComparisonUiState.Error(e.message ?: "Failed to compare seasons")
            }
        }
    }

    /**
     * Reset comparison
     */
    fun reset() {
        _uiState.value = ComparisonUiState.Initial
    }
}

/**
 * UI State for Comparison Screen
 */
sealed class ComparisonUiState {
    object Initial : ComparisonUiState()
    object Loading : ComparisonUiState()
    data class Success(val comparison: SeasonComparison) : ComparisonUiState()
    data class Error(val message: String) : ComparisonUiState()
}
