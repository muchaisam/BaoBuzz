package com.msdc.baobuzz.repository

import com.msdc.baobuzz.daos.CoachDao
import com.msdc.baobuzz.interfaces.FootballApi
import com.msdc.baobuzz.models.Coach
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoachRepository @Inject constructor(
    private val apiService: FootballApi,
    private val coachDao: CoachDao
) {
    companion object {
        private const val CACHE_DURATION = 30 * 24 * 60 * 60 * 1000L // 30 days for free tier
    }

    suspend fun getCoaches(ids: List<Int>): List<Coach> = coroutineScope {
        val cachedCoaches = coachDao.getCoachesByIds(ids)
        val validCachedCoaches = cachedCoaches.filter { !isDataStale(it.lastUpdated) }
        val missingIds = ids.filter { id -> validCachedCoaches.none { it.id == id } }

        if (missingIds.isEmpty()) {
            return@coroutineScope validCachedCoaches
        }

        val deferredCoaches = missingIds.map { id ->
            async {
                try {
                    val response = apiService.getCoach(id)
                    if (response.response.isNotEmpty()) {
                        response.response.first()
                    } else null
                } catch (e: Exception) {
                    null
                }
            }
        }

        val newCoaches = deferredCoaches.awaitAll().filterNotNull()
        if (newCoaches.isNotEmpty()) {
            coachDao.insertCoaches(newCoaches)
        }

        validCachedCoaches + newCoaches
    }

    private fun isDataStale(lastUpdated: Long): Boolean {
        return System.currentTimeMillis() - lastUpdated > CACHE_DURATION
    }
}
