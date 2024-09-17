package com.msdc.baobuzz.models

import kotlinx.serialization.Serializable

@Serializable
data class Fixture(
    val fixture: FixtureDetails,
    val league: League,
    val teams: Teams,
    val goals: Goals
)