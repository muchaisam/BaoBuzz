package com.msdc.baobuzz.presentation.transfers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msdc.baobuzz.core.api.FootballRepository
import com.msdc.baobuzz.core.models.TransferDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransfersViewModel @Inject constructor(private val repository: FootballRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow<TransfersUiState>(TransfersUiState.Idle)
    val uiState: StateFlow<TransfersUiState> = _uiState.asStateFlow()

    fun getTransfers(teamId: Int) {
        viewModelScope.launch {
            _uiState.value = TransfersUiState.Loading
            try {
                val result = repository.getTransfersByTeam(teamId)
                _uiState.value = TransfersUiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = TransfersUiState.Error(e.message ?: "Failed to load transfers")
            }
        }
    }
}

sealed class TransfersUiState {
    object Idle : TransfersUiState()
    object Loading : TransfersUiState()
    data class Success(val transfers: List<TransferDetails>) : TransfersUiState()
    data class Error(val message: String) : TransfersUiState()
}
