package com.msdc.baobuzz.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.msdc.baobuzz.models.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {
    suspend fun savePreferences(preferences: UserPreferences) {
        dataStore.edit { prefs ->
            prefs[SELECTED_LEAGUES] = preferences.selectedLeagueIds.joinToString(",")
            prefs[SELECTED_TEAMS] = preferences.selectedTeamIds.joinToString(",")
            prefs[TEAM_NOTIFICATIONS] =
                preferences.teamNotifications.entries.joinToString(",") {
                    "${it.key}:${it.value}"
                }
            prefs[ONBOARDING_COMPLETED] = preferences.isOnboardingCompleted
            prefs[PREFERRED_LANGUAGE] = preferences.preferredLanguage
            prefs[NOTIFICATIONS_ENABLED] = preferences.notificationsEnabled
        }
    }

    suspend fun markOnboardingComplete(selectedLeagueIds: List<Int>) {
        dataStore.edit { prefs ->
            prefs[SELECTED_LEAGUES] = selectedLeagueIds.joinToString(",")
            prefs[ONBOARDING_COMPLETED] = true
        }
    }

    suspend fun clearAllPreferences() {
        dataStore.edit { prefs -> prefs.clear() }
    }

    fun getPreferences(): Flow<UserPreferences> =
        dataStore.data.map { prefs ->
            UserPreferences(
                selectedLeagueIds =
                    prefs[SELECTED_LEAGUES]?.split(",")?.mapNotNull {
                        if (it.isBlank()) null else it.toIntOrNull()
                    }
                        ?: emptyList(),
                selectedTeamIds =
                    prefs[SELECTED_TEAMS]?.split(",")?.mapNotNull {
                        if (it.isBlank()) null else it.toIntOrNull()
                    }
                        ?: emptyList(),
                teamNotifications =
                    prefs[TEAM_NOTIFICATIONS]
                        ?.split(",")
                        ?.associate {
                            val parts = it.split(":")
                            if (parts.size == 2) {
                                parts[0].toInt() to parts[1].toBoolean()
                            } else {
                                0 to false
                            }
                        }
                        ?.filterKeys { it != 0 }
                        ?: emptyMap(),
                isOnboardingCompleted = prefs[ONBOARDING_COMPLETED] ?: false,
                preferredLanguage = prefs[PREFERRED_LANGUAGE] ?: "en",
                notificationsEnabled = prefs[NOTIFICATIONS_ENABLED] ?: true
            )
        }

    companion object {
        val SELECTED_LEAGUES = stringPreferencesKey("selected_leagues")
        val SELECTED_TEAMS = stringPreferencesKey("selected_teams")
        val TEAM_NOTIFICATIONS = stringPreferencesKey("team_notifications")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val PREFERRED_LANGUAGE = stringPreferencesKey("preferred_language")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }
}
