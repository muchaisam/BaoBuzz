package com.example.baobuzz.models

import kotlinx.serialization.Serializable

@Serializable
data class Fixture(
    val fixture: FixtureDetails,
    val league: League,
    val teams: Teams,
    val goals: Goals
)