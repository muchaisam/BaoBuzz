package com.msdc.baobuzz.models

data class UserPreferences(
    val selectedLeagueIds: List<Int>,
    val selectedTeamIds: List<Int>,
    val teamNotifications: Map<Int, Boolean>
)