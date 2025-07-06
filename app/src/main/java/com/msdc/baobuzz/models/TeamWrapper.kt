package com.msdc.baobuzz.models

import kotlinx.serialization.Serializable


@Serializable
data class TeamWrapper(
    val team: Team,
    val venue: Venue
)