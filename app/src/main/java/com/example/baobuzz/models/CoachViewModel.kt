package com.example.baobuzz.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.baobuzz.interfaces.CoachResult
import com.example.baobuzz.repository.CoachRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


// ViewModel
class CoachViewModel @Inject constructor(
    private val repository: CoachRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<CoachUiState>(CoachUiState.Loading)
    val uiState: StateFlow<CoachUiState> = _uiState.asStateFlow()

    private val _selectedCoach = MutableStateFlow<Coach?>(null)
    val selectedCoach: StateFlow<Coach?> = _selectedCoach.asStateFlow()


    fun loadCoaches(ids: List<Int>) {
        viewModelScope.launch {
            _uiState.value = CoachUiState.Loading
            try {
                val coaches = ids.mapNotNull { id ->
                    when (val result = repository.getCoach(id)) {
                        is CoachResult.Success -> result.data
                        is CoachResult.Error -> {
                            _uiState.value = CoachUiState.Error(result.exception.message ?: "Unknown error")
                            null
                        }
                    }
                }
                _uiState.value = CoachUiState.Success(coaches)
            } catch (e: Exception) {
                _uiState.value = CoachUiState.Error(e.message ?: "Unknown error")
            }

        }
    }

    fun selectCoach(coach: Coach) {
        _selectedCoach.value = coach
    }

    fun clearSelectedCoach() {
        _selectedCoach.value = null
    }
}
// ViewModelFactory
class CoachViewModelFactory @Inject constructor(
    private val repository: CoachRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CoachViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CoachViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


sealed class CoachUiState {
    object Loading : CoachUiState()
    data class Success(val coaches: List<Coach>) : CoachUiState()
    data class Error(val message: String) : CoachUiState()
}