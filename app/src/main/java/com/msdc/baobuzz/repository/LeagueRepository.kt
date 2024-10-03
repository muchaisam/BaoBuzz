package com.msdc.baobuzz.repository

import com.msdc.baobuzz.daos.AppDatabase
import com.msdc.baobuzz.interfaces.FootballApi
import com.msdc.baobuzz.models.League
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LeagueRepository(private val api: FootballApi, private val db: AppDatabase) {
    suspend fun getLeagues(forceRefresh: Boolean = false): List<League> = withContext(Dispatchers.IO) {
        if (forceRefresh || db.leagueDao().getAllLeagues().isEmpty()) {
            val leaguesResponse = api.getLeagues(current = true)
            val leagues = leaguesResponse.league
            db.leagueDao().insertAll(leagues)
        }
        db.leagueDao().getAllLeagues()
    }

    suspend fun searchLeagues(query: String): List<League> = withContext(Dispatchers.IO) {
        val searchResponse = api.searchLeagues(query)
        searchResponse.league
    }

    suspend fun getTopFiveLeagues(): List<League> = withContext(Dispatchers.IO) {
        db.leagueDao().getTopFiveLeagues()
    }
}