package com.example.baobuzz.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.lifecycle.*
import com.example.baobuzz.daos.Transfer
import com.example.baobuzz.factory.TransfersFactory
import com.example.baobuzz.interfaces.NetworkResult
import com.example.baobuzz.repository.TransfersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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