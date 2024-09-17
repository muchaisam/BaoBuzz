package com.msdc.baobuzz.models

data class StandingsResponse(
    val response: List<LeagueStandings>
)

data class LeagueStandings(
    val league: LeagueStanding
)

data class LeagueStanding(
    val id: Int,
    val name: String,
    val country: String,
    val logo: String,
    val flag: String,
    val season: Int,
    val standings: List<List<TeamStanding>>
)

data class TeamStanding(
    val rank: Int,
    val team: StandingTeam,
    val points: Int,
    val goalsDiff: Int,
    val group: String,
    val form: String,
    val status: String,
    val description: String?,
    val all: TeamStats,
    val home: TeamStats,
    val away: TeamStats,
    val update: String
)

data class StandingTeam(
    val id: Int,
    val name: String,
    val logo: String
)

data class TeamStats(
    val played: Int,
    val win: Int,
    val draw: Int,
    val lose: Int,
    val goals: StandingGoals
)

data class StandingGoals(
    val goalsfor: Int,
    val against: Int
)
