package com.msdc.baobuzz.features.facts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msdc.baobuzz.core.api.HistoricalRepository
import com.msdc.baobuzz.core.models.FactCategory
import com.msdc.baobuzz.core.models.HistoricalFact
import com.msdc.baobuzz.core.models.OnThisDayFact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * FactsViewModel - Powers the Historical Facts Feature 📖
 *
 * Features:
 * - Daily rotating facts
 * - "On This Day" historical events
 * - Category filtering (Records, Legends, Moments, etc.)
 * - Infinite scroll with pagination
 * - Share functionality
 */
@HiltViewModel
class FactsViewModel @Inject constructor(
    private val historicalRepository: HistoricalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FactsUiState>(FactsUiState.Loading)
    val uiState: StateFlow<FactsUiState> = _uiState.asStateFlow()

    private var currentCategory: FactCategory? = null
    private var allFacts: List<HistoricalFact> = emptyList()

    init {
        loadFacts()
    }

    /**
     * Load all facts and daily fact
     */
    private fun loadFacts() {
        viewModelScope.launch {
            _uiState.value = FactsUiState.Loading

            try {
                // Get daily fact
                val dailyFact = historicalRepository.getDailyFact()

                // Get "On This Day" facts
                val today = getCurrentDate()
                val onThisDayFacts = historicalRepository.getOnThisDayFacts(today)

                // Get all facts
                historicalRepository.getAllFacts().collect { facts ->
                    allFacts = facts
                    _uiState.value = FactsUiState.Success(
                        dailyFact = dailyFact,
                        onThisDayFacts = onThisDayFacts,
                        facts = facts,
                        selectedCategory = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = FactsUiState.Error(e.message ?: "Failed to load facts")
            }
        }
    }

    /**
     * Filter facts by category
     */
    fun filterByCategory(category: FactCategory?) {
        viewModelScope.launch {
            currentCategory = category

            val currentState = _uiState.value as? FactsUiState.Success ?: return@launch

            try {
                if (category == null) {
                    // Show all facts
                    _uiState.value = currentState.copy(
                        facts = allFacts,
                        selectedCategory = null
                    )
                } else {
                    // Filter by category
                    historicalRepository.getFactsByCategory(category).collect { facts ->
                        _uiState.value = currentState.copy(
                            facts = facts,
                            selectedCategory = category
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = FactsUiState.Error(e.message ?: "Failed to filter facts")
            }
        }
    }

    /**
     * Refresh facts
     */
    fun refresh() {
        loadFacts()
    }

    /**
     * Share a fact
     */
    fun shareFact(fact: HistoricalFact): String {
        return """
            ⚽ ${fact.title}

            ${fact.description}

            📅 ${fact.date}
            ${if (fact.league != null) "🏆 ${fact.league}" else ""}

            #Football #History #BaoBuzz
        """.trimIndent()
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}

/**
 * UI State for Facts Screen
 */
sealed class FactsUiState {
    object Loading : FactsUiState()

    data class Success(
        val dailyFact: HistoricalFact,
        val onThisDayFacts: List<OnThisDayFact>,
        val facts: List<HistoricalFact>,
        val selectedCategory: FactCategory?
    ) : FactsUiState()

    data class Error(val message: String) : FactsUiState()
}
