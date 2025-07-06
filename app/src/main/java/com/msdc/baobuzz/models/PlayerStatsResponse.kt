package com.msdc.baobuzz.models

import kotlinx.serialization.Serializable

@Serializable
data class TopScorersResponse(
    val response: List<PlayerStatResponse>
)

@Serializable
data class TopAssistersResponse(
    val response: List<PlayerStatResponse>
)

@Serializable
data class PlayerStatResponse(
    val player: PlayerInfo,
    val statistics: List<PlayerStatistics>
)

@Serializable
data class PlayerInfo(
    val id: Int,
    val name: String,
    val firstname: String?,
    val lastname: String?,
    val age: Int?,
    val birth: PlayerBirth?,
    val nationality: String?,
    val height: String?,
    val weight: String?,
    val injured: Boolean?,
    val photo: String?
)

@Serializable
data class PlayerBirth(
    val date: String?,
    val place: String?,
    val country: String?
)

@Serializable
data class PlayerStatistics(
    val team: TeamInfo,
    val league: PlayerLeagueInfo,
    val games: GameStats?,
    val goals: GoalStats?,
    val passes: PassStats?,
    val tackles: TackleStats?,
    val duels: DuelStats?,
    val dribbles: DribbleStats?,
    val fouls: FoulStats?,
    val cards: CardStats?,
    val penalty: PenaltyStats?
)

@Serializable
data class TeamInfo(
    val id: Int,
    val name: String,
    val logo: String
)

@Serializable
data class PlayerLeagueInfo(
    val id: Int,
    val name: String,
    val country: String,
    val logo: String,
    val flag: String?,
    val season: Int
)

@Serializable
data class GameStats(
    val appearences: Int?,
    val lineups: Int?,
    val minutes: Int?,
    val number: Int?,
    val position: String?,
    val rating: String?,
    val captain: Boolean?
)

@Serializable
data class GoalStats(
    val total: Int?,
    val conceded: Int?,
    val assists: Int?,
    val saves: Int?
)

@Serializable
data class PassStats(
    val total: Int?,
    val key: Int?,
    val accuracy: Int?
)

@Serializable
data class TackleStats(
    val total: Int?,
    val blocks: Int?,
    val interceptions: Int?
)

@Serializable
data class DuelStats(
    val total: Int?,
    val won: Int?
)

@Serializable
data class DribbleStats(
    val attempts: Int?,
    val success: Int?,
    val past: Int?
)

@Serializable
data class FoulStats(
    val drawn: Int?,
    val committed: Int?
)

@Serializable
data class CardStats(
    val yellow: Int?,
    val yellowred: Int?,
    val red: Int?
)

@Serializable
data class PenaltyStats(
    val won: Int?,
    val commited: Int?,
    val scored: Int?,
    val missed: Int?,
    val saved: Int?
)
