package com.msdc.baobuzz.models.openfootball

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * OpenFootball API Response Models
 *
 * Free historical football data from GitHub
 * All models marked @Immutable for Compose performance
 */

@Immutable
@Serializable
data class OFSeasonResponse(
    val name: String,
    val matches: List<OFMatch>? = null,
    val rounds: List<OFRound>? = null,
    val clubs: List<OFClub>? = null,
    val goals: List<OFGoal>? = null
)

@Immutable
@Serializable
data class OFMatch(
    val round: String,
    val date: String,
    val time: String? = null,
    val team1: String,
    val team2: String,
    val score: OFScore? = null,
    val goals1: List<OFGoal>? = null,
    val goals2: List<OFGoal>? = null
)

@Immutable
@Serializable
data class OFScore(
    val ft: List<Int>? = null,  // Full time: [home, away]
    val ht: List<Int>? = null,  // Half time: [home, away]
    val et: List<Int>? = null,  // Extra time: [home, away]
    val p: List<Int>? = null    // Penalties: [home, away]
)

@Immutable
@Serializable
data class OFGoal(
    val name: String,
    val team: String? = null,
    val minute: String? = null,
    val score: String? = null,
    val penalty: Boolean? = null,
    val owngoal: Boolean? = null,
    val offset: Int? = null
)

@Immutable
@Serializable
data class OFRound(
    val name: String,
    val matches: List<OFMatch>
)

@Immutable
@Serializable
data class OFClub(
    val name: String,
    val code: String? = null
)

// Extension functions to convert to domain models
fun OFMatch.toDomainMatch(seasonYear: String, leagueName: String): com.msdc.baobuzz.core.models.HistoricalMatch {
    val homeScore = score?.ft?.getOrNull(0) ?: 0
    val awayScore = score?.ft?.getOrNull(1) ?: 0

    return com.msdc.baobuzz.core.models.HistoricalMatch(
        id = "$date-${team1.replace(" ", "")}-${team2.replace(" ", "")}",
        homeTeam = team1,
        awayTeam = team2,
        homeScore = homeScore,
        awayScore = awayScore,
        date = date,
        round = round,
        season = seasonYear,
        league = leagueName,
        winner = when {
            homeScore > awayScore -> team1
            awayScore > homeScore -> team2
            else -> null
        }
    )
}

fun List<OFMatch>.toDomainMatches(seasonYear: String, leagueName: String): List<com.msdc.baobuzz.core.models.HistoricalMatch> {
    return this.mapNotNull { match ->
        try {
            match.toDomainMatch(seasonYear, leagueName)
        } catch (e: Exception) {
            null // Skip invalid matches
        }
    }
}
