package com.example.baobuzz.models

data class TeamConfig(val id: Int, val name: String)

object TeamConfigManager {
    private val teams = listOf(
        TeamConfig(33, "Manchester United"),
        TeamConfig(34, "Newcastle"),
        TeamConfig(40, "Liverpool"),
        TeamConfig(49, "Chelsea")
    )

    fun getTeams(): List<TeamConfig> = teams
}