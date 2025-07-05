package com.msdc.baobuzz.core.api

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

/** Tracks API requests to ensure we stay within the 100 requests/day limit */
@Singleton
class ApiRequestTracker @Inject constructor(private val dataStore: DataStore<Preferences>) {
    companion object {
        private const val MAX_DAILY_REQUESTS = 95 // Buffer for safety
        private val REQUEST_COUNT_KEY = intPreferencesKey("daily_request_count")
        private val LAST_RESET_DATE_KEY = stringPreferencesKey("last_reset_date")
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    suspend fun canMakeRequest(): Boolean {
        val today = dateFormat.format(Date())
        val prefs = dataStore.data.first()

        val lastResetDate = prefs[LAST_RESET_DATE_KEY]
        val requestCount = prefs[REQUEST_COUNT_KEY] ?: 0

        // Reset count if it's a new day
        if (lastResetDate != today) {
            resetDailyCount()
            return true
        }

        return requestCount < MAX_DAILY_REQUESTS
    }

    suspend fun recordRequest() {
        val today = dateFormat.format(Date())

        dataStore.edit { prefs ->
            val currentCount = prefs[REQUEST_COUNT_KEY] ?: 0
            prefs[REQUEST_COUNT_KEY] = currentCount + 1
            prefs[LAST_RESET_DATE_KEY] = today
        }
    }

    suspend fun getRemainingRequests(): Int {
        val prefs = dataStore.data.first()
        val requestCount = prefs[REQUEST_COUNT_KEY] ?: 0
        return (MAX_DAILY_REQUESTS - requestCount).coerceAtLeast(0)
    }

    private suspend fun resetDailyCount() {
        val today = dateFormat.format(Date())

        dataStore.edit { prefs ->
            prefs[REQUEST_COUNT_KEY] = 0
            prefs[LAST_RESET_DATE_KEY] = today
        }
    }
}
