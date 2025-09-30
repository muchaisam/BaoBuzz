package com.msdc.baobuzz.presentation.transfers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msdc.baobuzz.core.api.FootballRepository
import com.msdc.baobuzz.core.models.TransferDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransfersViewModel @Inject constructor(private val repository: FootballRepository) :
    ViewModel() {

    private val _transfers = MutableStateFlow<List<TransferDetails>>(emptyList())
    val transfers: StateFlow<List<TransferDetails>> = _transfers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getTransfers(teamId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getTransfersByTeam(teamId)
            _transfers.value = result
            _isLoading.value = false
        }
    }
}
