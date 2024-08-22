package com.example.baobuzz.models

data class LeagueWithFixtures(
    val id: Int,
    val name: String,
    val country: String,
    val flag: String?,
    val fixtures: List<Fixture>
)