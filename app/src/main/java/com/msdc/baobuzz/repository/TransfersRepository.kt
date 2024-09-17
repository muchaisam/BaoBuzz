package com.msdc.baobuzz.repository

import com.msdc.baobuzz.daos.Transfer
import com.msdc.baobuzz.daos.TransferDao
import com.msdc.baobuzz.interfaces.FootballApi
import com.msdc.baobuzz.interfaces.NetworkResult
import com.msdc.baobuzz.models.ApiTransfer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TransfersRepository @Inject constructor(
    private val apiService: FootballApi,
    private val transfersDao: TransferDao,
    @Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher
) {
    fun getTransfers(teamId: Int): Flow<NetworkResult<List<Transfer>>> = flow {
        val currentYear = LocalDate.now().year

        // Emit cached data immediately if available
        val cachedTransfers = transfersDao.getRecentTransfersForTeam(teamId, currentYear)
        if (cachedTransfers.isNotEmpty()) {
            emit(NetworkResult.Success(cachedTransfers))
        } else {
            emit(NetworkResult.Loading)
        }

        try {
            // Fetch all transfers from API
            val response = apiService.getTransfers(teamId)

            // Filter and map transfers
            val recentTransfers = response.response
                .filter { transfer ->
                    val transferYear = LocalDate.parse(transfer.transfers.first().date).year
                    transferYear == currentYear
                }
                .map { it.toTransfer(teamId) }

            // Cache recent transfers
            withContext(ioDispatcher) {
                transfersDao.insertTransfers(recentTransfers)
            }

            emit(NetworkResult.Success(recentTransfers))
        } catch (e: Exception) {
            Timber.e(e, "Error fetching transfers for team $teamId")
            when (e) {
                is IOException -> emit(NetworkResult.Error("Network error. Please check your connection."))
                is HttpException -> emit(NetworkResult.Error("API error. Please try again later."))
                else -> emit(NetworkResult.Error("An unexpected error occurred."))
            }
        }
    }.flowOn(ioDispatcher)
}


fun ApiTransfer.toTransfer(teamId: Int) = Transfer(
    id = this.player.id,
    teamId = teamId,
    playerName = this.player.name,
    fromTeam = this.transfers.first().teams.out.name,
    toTeam = this.transfers.first().teams.`in`.name,
    transferDate = this.transfers.first().date,
    transferType = this.transfers.first().type
)