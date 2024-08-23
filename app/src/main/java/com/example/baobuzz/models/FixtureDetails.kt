package com.example.baobuzz.models

import kotlinx.serialization.Serializable

@Serializable
data class FixtureDetails(
    val id: Int,
    val referee: String?,
    val timezone: String,
    val date: String,
    val timestamp: Long,
    val periods: Periods,
    val venue: Venue,
    val status: Status
)
