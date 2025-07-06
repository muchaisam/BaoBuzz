package com.msdc.baobuzz.core.models

import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val selectedLeagues: List<Int> = emptyList(),
    val favoriteTeams: List<Int> = emptyList(),
    val matchNotifications: Boolean = true,
    val transferNotifications: Boolean = true,
    val userName: String = ""
)