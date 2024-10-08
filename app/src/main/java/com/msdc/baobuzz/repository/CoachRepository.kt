package com.msdc.baobuzz.repository

import com.msdc.baobuzz.interfaces.FootballApi
import com.msdc.baobuzz.models.Coach
import com.msdc.baobuzz.daos.CoachDao
import com.msdc.baobuzz.interfaces.CoachResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoachRepository @Inject constructor(
    private val apiService: FootballApi,
    private val coachDao: CoachDao
) {
    suspend fun getCoach(id: Int): CoachResult<Coach> = withContext(Dispatchers.IO) {
        try {
            val cachedCoach = coachDao.getCoach(id)
            if (cachedCoach != null && !isDataStale(cachedCoach.lastUpdated)) {
                return@withContext CoachResult.Success(cachedCoach)
            }

            val apiResponse = apiService.getCoach(id)
            if (apiResponse.response.isNotEmpty()) {
                val remoteCoach = apiResponse.response.first()
                coachDao.insertCoach(remoteCoach)
                CoachResult.Success(remoteCoach)
            } else {
                CoachResult.Error(Exception("Coach not found"))
            }
        } catch (e: Exception) {
            CoachResult.Error(e)
        }
    }

    suspend fun getAllCachedCoaches(): List<Coach> = coachDao.getAllCoaches()

    private fun isDataStale(lastUpdated: Long): Boolean {
        val oneWeekInMillis = 7 * 24 * 60 * 60 * 1000
        return System.currentTimeMillis() - lastUpdated > oneWeekInMillis
    }
}


sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}