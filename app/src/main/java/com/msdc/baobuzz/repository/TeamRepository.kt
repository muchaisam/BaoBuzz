package com.msdc.baobuzz.repository

import android.util.Log
import com.msdc.baobuzz.daos.AppDatabase
import com.msdc.baobuzz.interfaces.FootballApi
import com.msdc.baobuzz.models.Team
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

class TeamRepository @Inject constructor(
    private val api: FootballApi,
    private val db: AppDatabase
) {
    suspend fun getTeamsForLeague(leagueId: Int, season: Int?): List<Team> = withContext(Dispatchers.IO) {
        try {
            val currentSeason = season ?: getCurrentSeason()
            val cachedTeams = db.teamDao().getTeamsForLeague(leagueId)

            if (cachedTeams.isEmpty()) {
                val response = api.getTeams(league = leagueId, season = currentSeason)
                val teams = response.response.map { teamWrapper ->
                    teamWrapper.team.copy().apply {
                        this.leagueId = leagueId
                    }
                }
                db.teamDao().insertAll(teams)
                teams
            } else {
                cachedTeams
            }
        } catch (e: Exception) {
            Timber.tag("TeamRepository").e(e, "Error fetching teams")
            emptyList()
        }
    }

    private fun getCurrentSeason(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.YEAR)
    }
}