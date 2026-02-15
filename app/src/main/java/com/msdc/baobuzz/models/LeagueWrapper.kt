package com.msdc.baobuzz.models

import kotlinx.serialization.Serializable

@Serializable
data class LeagueWrapper(
    val league: League,
    val country: Country,
    val seasons: List<Season>
)
