package com.msdc.baobuzz.core.models

// Shared data models used across the app

// League information
data class LeagueInfo(val id: Int, val name: String, val country: String, val flagUrl: String)

data class LiveMatch(
    val id: String,
    val homeTeam: Team,
    val awayTeam: Team,
    val homeScore: Int?,
    val awayScore: Int?,
    val status: String,
    val minute: Int?
)

data class TransferLegacy(
    val id: String,
    val player: PlayerLegacy,
    val fromTeam: TeamLegacy?,
    val toTeam: TeamLegacy,
    val transferType: String,
    val date: String,
    val fee: String?
)

data class TeamLegacy(val id: Int, val name: String, val logo: String)

data class PlayerLegacy(val id: Int, val name: String, val photo: String?)

data class LeagueStanding(
    val leagueId: Int,
    val leagueName: String,
    val leagueLogo: String,
    val topTeams: List<TeamStanding>
)

data class TeamStanding(
    val position: Int,
    val team: Team,
    val points: Int,
    val played: Int,
    val won: Int,
    val drawn: Int,
    val lost: Int
)

data class Fixture(
    val id: String,
    val homeTeam: Team,
    val awayTeam: Team,
    val date: String,
    val venue: String,
    val status: String,
    val homeScore: Int?,
    val awayScore: Int?
)

data class PlayerStat(
    val player: Player,
    val team: Team,
    val goals: Int,
    val assists: Int,
    val appearances: Int,
    val photo: String? = null
)

// Enhanced models for richer home screen content
data class SeasonSummary(
    val leagueId: Int,
    val leagueName: String,
    val leagueLogo: String,
    val season: Int,
    val champion: Team?,
    val topScorer: PlayerStat?,
    val totalGoals: Int,
    val totalMatches: Int,
    val isCurrentSeason: Boolean
)

data class UpcomingFixture(
    val id: String,
    val homeTeam: Team,
    val awayTeam: Team,
    val dateTime: String,
    val venue: String,
    val round: String?,
    val leagueId: Int,
    val leagueName: String
)

data class RecentResult(
    val id: String,
    val homeTeam: Team,
    val awayTeam: Team,
    val homeScore: Int,
    val awayScore: Int,
    val date: String,
    val round: String?,
    val leagueId: Int,
    val leagueName: String
)

data class LeagueInsight(
    val leagueId: Int,
    val leagueName: String,
    val leagueLogo: String,
    val currentStanding: TeamStanding?,
    val nextFixture: UpcomingFixture?,
    val lastResult: RecentResult?,
    val topScorer: PlayerStat?,
    val matchesPlayed: Int,
    val matchesRemaining: Int
)
