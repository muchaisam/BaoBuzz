package com.msdc.baobuzz.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.msdc.baobuzz.models.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    suspend fun savePreferences(preferences: UserPreferences) {
        dataStore.edit { prefs ->
            prefs[SELECTED_LEAGUES] = preferences.selectedLeagueIds.joinToString(",")
            prefs[SELECTED_TEAMS] = preferences.selectedTeamIds.joinToString(",")
            prefs[TEAM_NOTIFICATIONS] = preferences.teamNotifications.entries.joinToString(",") { "${it.key}:${it.value}" }
        }
    }

    fun getPreferences(): Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            selectedLeagueIds = prefs[SELECTED_LEAGUES]?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList(),
            selectedTeamIds = prefs[SELECTED_TEAMS]?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList(),
            teamNotifications = prefs[TEAM_NOTIFICATIONS]?.split(",")?.associate {
                val (id, enabled) = it.split(":")
                id.toInt() to enabled.toBoolean()
            } ?: emptyMap()
        )
    }

    companion object {
        val SELECTED_LEAGUES = stringPreferencesKey("selected_leagues")
        val SELECTED_TEAMS = stringPreferencesKey("selected_teams")
        val TEAM_NOTIFICATIONS = stringPreferencesKey("team_notifications")
    }
}