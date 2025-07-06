package com.msdc.baobuzz.models

data class UserPreferences(
    val selectedLeagueIds: List<Int> = emptyList(),
    val selectedTeamIds: List<Int> = emptyList(),
    val teamNotifications: Map<Int, Boolean> = emptyMap(),
    val isOnboardingCompleted: Boolean = false,
    val preferredLanguage: String = "en",
    val notificationsEnabled: Boolean = true
)