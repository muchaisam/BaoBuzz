package com.msdc.baobuzz.core.api

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/** Tracks API requests to enforce both daily and per-minute rate limits */
@Singleton
class ApiRequestTracker @Inject constructor(private val dataStore: DataStore<Preferences>) {
    companion object {
        private const val MAX_DAILY_REQUESTS = 95
        private const val MAX_REQUESTS_PER_MINUTE = 9
        private val REQUEST_COUNT_KEY = intPreferencesKey("daily_request_count")
        private val LAST_RESET_DATE_KEY = stringPreferencesKey("last_reset_date")
        private val MINUTE_REQUEST_COUNT_KEY = intPreferencesKey("minute_request_count")
        private val MINUTE_WINDOW_START_KEY = longPreferencesKey("minute_window_start")
    }

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    suspend fun canMakeRequest(): Boolean {
        val today = LocalDate.now().format(dateFormatter)
        val prefs = dataStore.data.first()
        val now = System.currentTimeMillis()

        val lastResetDate = prefs[LAST_RESET_DATE_KEY]
        val requestCount = prefs[REQUEST_COUNT_KEY] ?: 0

        // Reset count if it's a new day
        if (lastResetDate != today) {
            resetDailyCount()
            return true
        }

        // Check daily limit
        if (requestCount >= MAX_DAILY_REQUESTS) {
            Timber.w("Daily API limit reached (%d/%d)", requestCount, MAX_DAILY_REQUESTS)
            return false
        }

        // Check per-minute limit
        val minuteWindowStart = prefs[MINUTE_WINDOW_START_KEY] ?: 0L
        val minuteCount = prefs[MINUTE_REQUEST_COUNT_KEY] ?: 0

        if (now - minuteWindowStart < 60_000L && minuteCount >= MAX_REQUESTS_PER_MINUTE) {
            Timber.w("Per-minute API limit reached (%d/%d)", minuteCount, MAX_REQUESTS_PER_MINUTE)
            return false
        }

        return true
    }

    suspend fun recordRequest() {
        val today = LocalDate.now().format(dateFormatter)
        val now = System.currentTimeMillis()

        dataStore.edit { prefs ->
            val currentCount = prefs[REQUEST_COUNT_KEY] ?: 0
            prefs[REQUEST_COUNT_KEY] = currentCount + 1
            prefs[LAST_RESET_DATE_KEY] = today

            // Track per-minute requests
            val minuteWindowStart = prefs[MINUTE_WINDOW_START_KEY] ?: 0L
            if (now - minuteWindowStart >= 60_000L) {
                // New minute window
                prefs[MINUTE_WINDOW_START_KEY] = now
                prefs[MINUTE_REQUEST_COUNT_KEY] = 1
            } else {
                val minuteCount = prefs[MINUTE_REQUEST_COUNT_KEY] ?: 0
                prefs[MINUTE_REQUEST_COUNT_KEY] = minuteCount + 1
            }
        }
    }

    suspend fun getRemainingRequests(): Int {
        val prefs = dataStore.data.first()
        val requestCount = prefs[REQUEST_COUNT_KEY] ?: 0
        return (MAX_DAILY_REQUESTS - requestCount).coerceAtLeast(0)
    }

    private suspend fun resetDailyCount() {
        val today = LocalDate.now().format(dateFormatter)

        dataStore.edit { prefs ->
            prefs[REQUEST_COUNT_KEY] = 0
            prefs[LAST_RESET_DATE_KEY] = today
            prefs[MINUTE_REQUEST_COUNT_KEY] = 0
            prefs[MINUTE_WINDOW_START_KEY] = System.currentTimeMillis()
        }
    }
}
