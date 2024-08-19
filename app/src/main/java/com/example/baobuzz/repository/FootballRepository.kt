package com.example.baobuzz.repository

import android.util.Log
import com.example.baobuzz.api.ApiClient
import com.example.baobuzz.models.Fixture
import com.github.michaelbull.retry.policy.binaryExponentialBackoff
import com.github.michaelbull.retry.policy.constantDelay
import com.github.michaelbull.retry.policy.plus
import com.github.michaelbull.retry.retry
import io.github.oshai.kotlinlogging.KotlinLogging
import com.github.michaelbull.retry.policy.stopAtAttempts

private val logger = KotlinLogging.logger {}

class FootballRepository {
    private val api = ApiClient.footballApi
    private val retryPolicy = constantDelay<Throwable>(delayMillis = 1000L) + stopAtAttempts(10)
    suspend fun getUpcomingFixtures(leagueId: Int, next: Int = 10): List<Fixture> {
        return try {
            retry(retryPolicy) {
                val response = api.getUpcomingFixtures(leagueId, next, "Your_Timezone")
                response.response.filter { it.fixture.status.short == "NS" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Error fetching fixtures for league $leagueId" }
            emptyList()
        }
    }
}