package com.example.baobuzz.models

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
    val form: String,
    val description: String?
)

data class StandingTeam(
    val id: Int,
    val name: String,
    val logo: String
)