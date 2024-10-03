package com.msdc.baobuzz.repository

import com.msdc.baobuzz.daos.AppDatabase
import com.msdc.baobuzz.interfaces.FootballApi
import com.msdc.baobuzz.models.Team

class TeamRepository(private val api: FootballApi, private val db: AppDatabase) {
    suspend fun getTeamsForLeague(leagueId: Int, season: Int): List<Team> {
        val cachedTeams = db.teamDao().getTeamsForLeague(leagueId)
        if (cachedTeams.isEmpty()) {
            val teamsResponse = api.getTeams(league = leagueId, season = season)
            val teams = teamsResponse.team
            db.teamDao().insertAll(teams)
            return teams
        }
        return cachedTeams
    }

    suspend fun searchTeams(query: String): List<Team> {
        val searchResponse = api.searchTeams(query)
        return searchResponse.team
    }
}