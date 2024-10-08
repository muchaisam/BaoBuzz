package com.msdc.baobuzz.repository

import com.msdc.baobuzz.daos.AppDatabase
import com.msdc.baobuzz.interfaces.FootballApi
import com.msdc.baobuzz.models.League
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LeagueRepository @Inject constructor(
    private val api: FootballApi,
    private val db: AppDatabase
) {
    companion object {
        private val TOP_FIVE_LEAGUE_IDS = listOf(39, 140, 78, 135, 61)
    }

    suspend fun getTopFiveLeagues(): List<League> = withContext(Dispatchers.IO) {
        try {
            // First, try to get from cache
            val cachedLeagues = db.leagueDao().getTopFiveLeagues()
            if (cachedLeagues.isNotEmpty() && cachedLeagues.size == TOP_FIVE_LEAGUE_IDS.size) {
                return@withContext cachedLeagues
            }

            // If cache is empty or incomplete, fetch from API
            val allLeagues = mutableListOf<League>()
            TOP_FIVE_LEAGUE_IDS.forEach { leagueId ->
                try {
                    val response = api.getLeague(id = leagueId)
                    response.response.firstOrNull()?.let { wrapper ->
                        allLeagues.add(wrapper.league)
                    }
                } catch (e: Exception) {
                    // Log error but continue with other leagues
                    e.printStackTrace()
                }
            }

            // Cache the results
            if (allLeagues.isNotEmpty()) {
                db.leagueDao().insertAll(allLeagues)
            }

            allLeagues
        } catch (e: Exception) {
            // If everything fails, return empty list
            emptyList()
        }
    }
}