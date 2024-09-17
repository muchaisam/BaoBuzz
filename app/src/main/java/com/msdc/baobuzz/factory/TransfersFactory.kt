package com.msdc.baobuzz.factory

import com.msdc.baobuzz.daos.Transfer
import com.msdc.baobuzz.interfaces.NetworkResult
import com.msdc.baobuzz.models.TeamConfigManager
import com.msdc.baobuzz.repository.TransfersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TransfersFactory @Inject constructor(
    private val repository: TransfersRepository
) {
    fun getTransfersForTeam(teamId: Int): Flow<NetworkResult<List<Transfer>>> {
        return repository.getTransfers(teamId)
    }

    fun getTransfersForAllTeams(): Flow<Map<Int, NetworkResult<List<Transfer>>>> = flow {
        val results = TeamConfigManager.getTeams().associate {
            it.id to NetworkResult.Loading as NetworkResult<List<Transfer>>
        }.toMutableMap()
        emit(results)

        TeamConfigManager.getTeams().forEach { team ->
            getTransfersForTeam(team.id).collect { result ->
                results[team.id] = result
                emit(results.toMap())
            }
        }
    }
}