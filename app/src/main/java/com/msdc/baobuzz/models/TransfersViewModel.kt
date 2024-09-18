package com.msdc.baobuzz.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.msdc.baobuzz.daos.Transfer
import com.msdc.baobuzz.factory.TransfersFactory
import com.msdc.baobuzz.interfaces.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TransfersViewModel @Inject constructor(
    private val transfersFactory: TransfersFactory
) : ViewModel() {
    private val _transfersState = MutableStateFlow<Map<Int, NetworkResult<List<Transfer>>>>(emptyMap())
    val transfersState: StateFlow<Map<Int, NetworkResult<List<Transfer>>>> = _transfersState

    private val _selectedTeam = MutableStateFlow<TeamConfig?>(null)
    val selectedTeam: StateFlow<TeamConfig?> = _selectedTeam

    init {
        loadAllTransfers()
    }

    fun selectTeam(team: TeamConfig) {
        _selectedTeam.value = team
    }

    private fun loadAllTransfers() {
        viewModelScope.launch {
            transfersFactory.getTransfersForAllTeams().collect { result ->
                _transfersState.value = result
            }
        }
    }

    fun retry() {
        loadAllTransfers()
    }
}