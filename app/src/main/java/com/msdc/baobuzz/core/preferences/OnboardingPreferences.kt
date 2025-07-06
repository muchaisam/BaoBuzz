package com.msdc.baobuzz.core.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.msdc.baobuzz.core.models.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class OnboardingPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
        private val USER_PREFERENCES_KEY = stringPreferencesKey("user_preferences")
    }

    private val json = Json { ignoreUnknownKeys = true }

    val isOnboardingCompleted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[ONBOARDING_COMPLETED_KEY] ?: false
    }

    fun getUserPreferences(): Flow<UserPreferences> = dataStore.data.map { prefs ->
        val preferencesJson = prefs[USER_PREFERENCES_KEY] ?: ""
        if (preferencesJson.isBlank()) {
            UserPreferences()
        } else {
            try {
                json.decodeFromString<UserPreferences>(preferencesJson)
            } catch (e: Exception) {
                UserPreferences()
            }
        }
    }

    suspend fun saveUserPreferences(userPreferences: UserPreferences) {
        dataStore.edit { prefs ->
            prefs[USER_PREFERENCES_KEY] = json.encodeToString(userPreferences)
        }
    }

    suspend fun completeOnboarding() {
        dataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETED_KEY] = true
        }
    }

    suspend fun clearOnboarding() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
