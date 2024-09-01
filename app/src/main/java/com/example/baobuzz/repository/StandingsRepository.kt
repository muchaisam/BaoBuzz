package com.example.baobuzz.repository

import com.example.baobuzz.daos.AppDatabase
import com.example.baobuzz.entity.CachedStanding
import com.example.baobuzz.interfaces.FootballApi
import com.example.baobuzz.models.LeagueStandings
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StandingsRepository(private val api: FootballApi,private val database: AppDatabase) {
    private val gson = Gson()

    suspend fun getStandings(leagueId: Int, season: Int): LeagueStandings? {
        return withContext(Dispatchers.IO) {
            val cachedStanding = database.standingsDao().getStandings(leagueId, season)

            if (cachedStanding != null && System.currentTimeMillis() - cachedStanding.lastUpdated < 12 * 60 * 60 * 1000) {
                // Use cached data if it's less than 12 hours old
                return@withContext gson.fromJson(cachedStanding.standingsJson, LeagueStandings::class.java)
            }

            // Fetch new data from API
            try {
                val response = api.getStandings(leagueId, season)
                val leagueStandings = response.response.firstOrNull()
                if (leagueStandings != null) {
                    // Cache the new data
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
                // Log the error and return null or cached data if available
                e.printStackTrace()
                if (cachedStanding != null) {
                    gson.fromJson(cachedStanding.standingsJson, LeagueStandings::class.java)
                } else {
                    null
                }
            }
        }
    }
}