package com.msdc.baobuzz.models.footballdata

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * Football-Data.org API v4 Response Models
 *
 * Free tier: 10 requests per minute
 * All models marked @Immutable for Compose performance
 */

// ================== Competitions ==================

@Immutable
@Serializable
data class FDCompetitionsResponse(
    val count: Int,
    val competitions: List<FDCompetition>
)

@Immutable
@Serializable
data class FDCompetition(
    val id: Int,
    val name: String,
    val code: String,
    val type: String,
    val emblem: String? = null,
    val area: FDArea? = null,
    val currentSeason: FDSeason? = null
)

@Immutable
@Serializable
data class FDArea(
    val id: Int,
    val name: String,
    val code: String? = null,
    val flag: String? = null
)

@Immutable
@Serializable
data class FDSeason(
    val id: Int,
    val startDate: String,
    val endDate: String,
    val currentMatchday: Int? = null,
    val winner: FDTeamInfo? = null
)

// ================== Standings ==================

@Immutable
@Serializable
data class FDStandingsResponse(
    val competition: FDCompetition,
    val season: FDSeason,
    val standings: List<FDStandingTable>
)

@Immutable
@Serializable
data class FDStandingTable(
    val stage: String,
    val type: String,
    val group: String? = null,
    val table: List<FDTableEntry>
)

@Immutable
@Serializable
data class FDTableEntry(
    val position: Int,
    val team: FDTeamInfo,
    val playedGames: Int,
    val form: String? = null,
    val won: Int,
    val draw: Int,
    val lost: Int,
    val points: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val goalDifference: Int
)

@Immutable
@Serializable
data class FDTeamInfo(
    val id: Int,
    val name: String,
    val shortName: String? = null,
    val tla: String? = null,
    val crest: String? = null
)

// ================== Matches ==================

@Immutable
@Serializable
data class FDMatchesResponse(
    val matches: List<FDMatchInfo>
)

@Immutable
@Serializable
data class FDMatchResponse(
    val match: FDMatchDetail
)

@Immutable
@Serializable
data class FDMatchInfo(
    val id: Int,
    val utcDate: String,
    val status: String,
    val matchday: Int? = null,
    val stage: String? = null,
    val group: String? = null,
    val lastUpdated: String,
    val homeTeam: FDTeamInfo,
    val awayTeam: FDTeamInfo,
    val score: FDScore,
    val competition: FDCompetition? = null,
    val season: FDSeason? = null
)

@Immutable
@Serializable
data class FDMatchDetail(
    val id: Int,
    val utcDate: String,
    val status: String,
    val matchday: Int? = null,
    val stage: String? = null,
    val group: String? = null,
    val lastUpdated: String,
    val homeTeam: FDTeamInfo,
    val awayTeam: FDTeamInfo,
    val score: FDScore,
    val goals: List<FDGoal>? = null,
    val bookings: List<FDBooking>? = null,
    val substitutions: List<FDSubstitution>? = null,
    val referees: List<FDReferee>? = null,
    val competition: FDCompetition,
    val season: FDSeason
)

@Immutable
@Serializable
data class FDScore(
    val winner: String? = null,
    val duration: String? = null,
    val fullTime: FDScoreDetail? = null,
    val halfTime: FDScoreDetail? = null,
    val extraTime: FDScoreDetail? = null,
    val penalties: FDScoreDetail? = null
)

@Immutable
@Serializable
data class FDScoreDetail(
    val home: Int? = null,
    val away: Int? = null
)

@Immutable
@Serializable
data class FDGoal(
    val minute: Int,
    val injuryTime: Int? = null,
    val type: String,
    val team: FDTeamInfo,
    val scorer: FDPlayer,
    val assist: FDPlayer? = null,
    val score: FDScoreDetail
)

@Immutable
@Serializable
data class FDBooking(
    val minute: Int,
    val team: FDTeamInfo,
    val player: FDPlayer,
    val card: String
)

@Immutable
@Serializable
data class FDSubstitution(
    val minute: Int,
    val team: FDTeamInfo,
    val playerOut: FDPlayer,
    val playerIn: FDPlayer
)

@Immutable
@Serializable
data class FDReferee(
    val id: Int,
    val name: String,
    val type: String,
    val nationality: String? = null
)

// ================== Scorers ==================

@Immutable
@Serializable
data class FDScorersResponse(
    val count: Int,
    val competition: FDCompetition,
    val season: FDSeason,
    val scorers: List<FDScorer>
)

@Immutable
@Serializable
data class FDScorer(
    val player: FDPlayer,
    val team: FDTeamInfo,
    val goals: Int,
    val assists: Int? = null,
    val penalties: Int? = null
)

@Immutable
@Serializable
data class FDPlayer(
    val id: Int,
    val name: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val dateOfBirth: String? = null,
    val nationality: String? = null,
    val position: String? = null,
    val shirtNumber: Int? = null,
    val section: String? = null
)

// ================== Teams ==================

@Immutable
@Serializable
data class FDTeamResponse(
    val id: Int,
    val name: String,
    val shortName: String,
    val tla: String,
    val crest: String? = null,
    val address: String? = null,
    val website: String? = null,
    val founded: Int? = null,
    val clubColors: String? = null,
    val venue: String? = null,
    val area: FDArea,
    val runningCompetitions: List<FDCompetition>? = null,
    val coach: FDCoach? = null,
    val squad: List<FDPlayer>? = null,
    val staff: List<FDStaff>? = null
)

@Immutable
@Serializable
data class FDCoach(
    val id: Int,
    val firstName: String? = null,
    val lastName: String? = null,
    val name: String,
    val dateOfBirth: String? = null,
    val nationality: String? = null,
    val contract: FDContract? = null
)

@Immutable
@Serializable
data class FDContract(
    val start: String,
    val until: String
)

@Immutable
@Serializable
data class FDStaff(
    val id: Int,
    val firstName: String? = null,
    val lastName: String? = null,
    val name: String,
    val dateOfBirth: String? = null,
    val nationality: String? = null
)

// ================== Extension Functions to Domain Models ==================

fun FDMatchInfo.toDomainFixture(): com.msdc.baobuzz.core.models.Fixture {
    return com.msdc.baobuzz.core.models.Fixture(
        id = id.toString(),
        homeTeam = homeTeam.toDomainTeam(),
        awayTeam = awayTeam.toDomainTeam(),
        date = utcDate,
        venue = "", // Not provided in match info
        status = status,
        homeScore = score.fullTime?.home,
        awayScore = score.fullTime?.away
    )
}

fun FDTeamInfo.toDomainTeam(): com.msdc.baobuzz.models.Team {
    return com.msdc.baobuzz.models.Team(
        id = id,
        name = name,
        code = tla,
        country = "", // Not in basic team info
        founded = null,
        national = false,
        logo = crest ?: ""
    )
}

fun FDTableEntry.toDomainTeamStanding(): com.msdc.baobuzz.core.models.TeamStanding {
    return com.msdc.baobuzz.core.models.TeamStanding(
        position = position,
        team = team.toDomainTeam(),
        points = points,
        played = playedGames,
        won = won,
        drawn = draw,
        lost = lost
    )
}

fun FDScorer.toDomainPlayerStat(): com.msdc.baobuzz.core.models.PlayerStat {
    return com.msdc.baobuzz.core.models.PlayerStat(
        player = com.msdc.baobuzz.core.models.Player(
            id = player.id,
            name = player.name,
            photo = null,
            position = player.position ?: "Unknown",
            nationality = player.nationality ?: "Unknown"
        ),
        team = team.toDomainTeam(),
        goals = goals,
        assists = assists ?: 0,
        appearances = 0, // Not provided in scorer response
        photo = null
    )
}
