package com.msdc.baobuzz.models


@kotlinx.serialization.Serializable
data class TeamWrapper(
    val team: Team,
    val venue: Venue
)