package com.msdc.baobuzz.repository

import com.google.gson.Gson
import com.msdc.baobuzz.daos.AppDatabase
import com.msdc.baobuzz.entity.CachedStanding
import com.msdc.baobuzz.interfaces.FootballApi
import com.msdc.baobuzz.models.CachedTopAssisters
import com.msdc.baobuzz.models.CachedTopScorers
import com.msdc.baobuzz.models.LeagueStandings
import com.msdc.baobuzz.models.PlayerStatResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class StandingsRepository(private val api: FootballApi, private val database: AppDatabase) {
    private val gson = Gson()
    private val cacheValidityPeriod = TimeUnit.HOURS.toMillis(12)

    suspend fun getStandings(leagueId: Int, season: Int): LeagueStandings? {
        return withContext(Dispatchers.IO) {
            val cachedData = database.standingsDao().getStandings(leagueId, season)

            if (cachedData != null && (System.currentTimeMillis() - cachedData.lastUpdated < cacheValidityPeriod)) {
                return@withContext gson.fromJson(
                    cachedData.standingsJson,
                    LeagueStandings::class.java
                )
            }

            try {
                val response = api.getStandings(leagueId, season)
                val leagueStandings = response.response.firstOrNull()
                if (leagueStandings != null) {
                    val standingsJson = gson.toJson(leagueStandings)
                    database.standingsDao().insertStandings(
                        CachedStanding(
                            id = "${leagueId}_$season",
                            leagueId = leagueId,
                            season = season,
                            standingsJson = standingsJson,
                            lastUpdated = System.currentTimeMillis()
                        )
                    )
                }
                leagueStandings
            } catch (e: Exception) {
                e.printStackTrace()
                if (cachedData != null) {
                    gson.fromJson(cachedData.standingsJson, LeagueStandings::class.java)
                } else {
                    null
                }
            }
        }
    }

    suspend fun getTopScorers(leagueId: Int, season: Int): List<PlayerStatResponse> {
        return withContext(Dispatchers.IO) {
            val cachedData = database.topScorersDao().getTopScorers(leagueId, season)

            if (cachedData != null && (System.currentTimeMillis() - cachedData.lastUpdated < cacheValidityPeriod)) {
                return@withContext gson.fromJson(
                    cachedData.topScorersJson,
                    Array<PlayerStatResponse>::class.java
                ).toList()
            }

            try {
                val response = api.getTopScorers(leagueId, season)
                val topScorers = response.response
                if (topScorers.isNotEmpty()) {
                    val topScorersJson = gson.toJson(topScorers)
                    database.topScorersDao().insertTopScorers(
                        CachedTopScorers(
                            id = "${leagueId}_${season}_scorers",
                            leagueId = leagueId,
                            season = season,
                            topScorersJson = topScorersJson,
                            lastUpdated = System.currentTimeMillis()
                        )
                    )
                }
                topScorers
            } catch (e: Exception) {
                e.printStackTrace()
                if (cachedData != null) {
                    gson.fromJson(cachedData.topScorersJson, Array<PlayerStatResponse>::class.java)
                        .toList()
                } else {
                    emptyList()
                }
            }
        }
    }

    suspend fun getTopAssisters(leagueId: Int, season: Int): List<PlayerStatResponse> {
        return withContext(Dispatchers.IO) {
            val cachedData = database.topAssistersDao().getTopAssisters(leagueId, season)

            if (cachedData != null && (System.currentTimeMillis() - cachedData.lastUpdated < cacheValidityPeriod)) {
                return@withContext gson.fromJson(
                    cachedData.topAssistersJson,
                    Array<PlayerStatResponse>::class.java
                ).toList()
            }

            try {
                val response = api.getTopAssisters(leagueId, season)
                val topAssisters = response.response
                if (topAssisters.isNotEmpty()) {
                    val topAssistersJson = gson.toJson(topAssisters)
                    database.topAssistersDao().insertTopAssisters(
                        CachedTopAssisters(
                            id = "${leagueId}_${season}_assisters",
                            leagueId = leagueId,
                            season = season,
                            topAssistersJson = topAssistersJson,
                            lastUpdated = System.currentTimeMillis()
                        )
                    )
                }
                topAssisters
            } catch (e: Exception) {
                e.printStackTrace()
                if (cachedData != null) {
                    gson.fromJson(
                        cachedData.topAssistersJson,
                        Array<PlayerStatResponse>::class.java
                    ).toList()
                } else {
                    emptyList()
                }
            }
        }
    }
}