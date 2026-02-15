package com.msdc.baobuzz.models

import androidx.compose.runtime.Immutable

// ✅ All player stats models marked @Immutable for Compose performance

@Immutable
data class PlayerStats(
    val player: PlayerStat,
    val statistics: List<Statistics>
)

@Immutable
data class PlayerStat(
    val id: Int,
    val name: String,
    val firstname: String,
    val lastname: String,
    val age: Int,
    val birth: Birth?,
    val nationality: String,
    val height: String,
    val weight: String,
    val injured: Boolean,
    val photo: String
)

@Immutable
data class Birth(
    val date: String,
    val place: String?,
    val country: String?
)

@Immutable
data class Statistics(
    val team: PlayerTeam,
    val league: PlayerLeague,
    val games: Games,
    val substitutes: Substitutes,
    val shots: Shots,
    val goals: PlayerGoals,
    val passes: Passes,
    val tackles: Tackles,
    val duels: Duels,
    val dribbles: Dribbles,
    val fouls: Fouls,
    val cards: Cards,
    val penalty: Penalty
)

@Immutable
data class PlayerTeam(
    val id: Int,
    val name: String,
    val logo: String
)

@Immutable
data class PlayerLeague(
    val id: Int,
    val name: String,
    val country: String,
    val logo: String,
    val flag: String?,
    val season: Int
)

@Immutable
data class Games(
    val appearances: Int?,
    val lineups: Int?,
    val minutes: Int?,
    val number: Int?,
    val position: String?,
    val rating: String?,
    val captain: Boolean
)

@Immutable
data class Substitutes(
    val `in`: Int?,
    val out: Int?,
    val bench: Int?
)

@Immutable
data class Shots(
    val total: Int?,
    val on: Int?
)

@Immutable
data class PlayerGoals(
    val total: Int?,
    val conceded: Int?,
    val assists: Int?,
    val saves: Int?
)

@Immutable
data class Passes(
    val total: Int?,
    val key: Int?,
    val accuracy: Int?
)

@Immutable
data class Tackles(
    val total: Int?,
    val blocks: Int?,
    val interceptions: Int?
)

@Immutable
data class Duels(
    val total: Int?,
    val won: Int?
)

@Immutable
data class Dribbles(
    val attempts: Int?,
    val success: Int?,
    val past: Int?
)

@Immutable
data class Fouls(
    val drawn: Int?,
    val committed: Int?
)

@Immutable
data class Cards(
    val yellow: Int?,
    val yellowred: Int?,
    val red: Int?
)

@Immutable
data class Penalty(
    val won: Int?,
    val committed: Int?,
    val scored: Int?,
    val missed: Int?,
    val saved: Int?
)