package com.msdc.baobuzz.models

import kotlinx.serialization.Serializable


@Serializable
data class Fixture(
    val fixture: FixtureDetails,
    val league: League,
    val teams: Teams,
    val goals: Goals,
    val score: Score,
    val events: List<Event>? = null,
    val lineups: List<Lineup>? = null,
    val statistics: List<Statistic>? = null,
    val predictions: Prediction? = null
)

@Serializable
data class Event(
    val time: EventTime,
    val team: Team,
    val player: Player,
    val assist: Player?,
    val type: String,
    val detail: String
)
@Serializable
data class EventTime(
    val elapsed: Int,
    val extra: Int?
)

@Serializable
data class Lineup(
    val team: Team,
    val formation: String,
    val startXI: List<Player>,
    val substitutes: List<Player>,
    val coach: TeamCoach
)

@Serializable
data class TeamCoach(
    val id: Int?,
    val name: String,
    val photo: String?
)
@Serializable
data class Statistic(
    val team: Team,
    val statistics: List<StatItem>
)

@Serializable
data class StatItem(
    val type: String,
    val value: String?
)