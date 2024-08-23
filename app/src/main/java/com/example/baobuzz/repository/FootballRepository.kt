package com.example.baobuzz.repository

import android.util.Log
import com.example.baobuzz.api.ApiClient
import com.example.baobuzz.interfaces.FootballApi
import com.example.baobuzz.models.Fixture
import com.github.michaelbull.retry.policy.binaryExponentialBackoff
import com.github.michaelbull.retry.policy.constantDelay
import com.github.michaelbull.retry.policy.plus
import com.github.michaelbull.retry.retry
import io.github.oshai.kotlinlogging.KotlinLogging
import com.github.michaelbull.retry.policy.stopAtAttempts
import androidx.room.*
import com.example.baobuzz.daos.AppDatabase
import com.example.baobuzz.daos.CachedFixture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import com.example.baobuzz.interfaces.Result
import kotlinx.serialization.encodeToString
import java.util.TimeZone

private val logger = KotlinLogging.logger {}


class FootballRepository(private val api: FootballApi, private val db: AppDatabase) {
    private val fixtureDao = db.fixtureDao()
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getUpcomingFixtures(leagueId: Int, next: Int = 10): Result<List<Fixture>> {
        return withContext(Dispatchers.IO) {
            try {
                val cachedFixtures = fixtureDao.getFixturesForLeague(leagueId)
                if (cachedFixtures.isNotEmpty() && !isCacheExpired(cachedFixtures.first().lastUpdated)) {
                    Result.Success(cachedFixtures.map { json.decodeFromString(it.fixtureData) })
                } else {
                    val fixtures = fetchAndCacheFixtures(leagueId, next)
                    Result.Success(fixtures)
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    private suspend fun fetchAndCacheFixtures(leagueId: Int, next: Int): List<Fixture> {
        val response = api.getUpcomingFixtures(leagueId, next, TimeZone.getDefault().id)
        val fixtures = response.response.filter { it.fixture.status.short == "NS" }

        fixtureDao.deleteFixturesForLeague(leagueId)
        fixtureDao.insertFixtures(fixtures.map {
            CachedFixture(
                id = it.fixture.id,
                leagueId = leagueId,
                fixtureData = json.encodeToString(it),
                lastUpdated = System.currentTimeMillis()
            )
        })

        return fixtures
    }


    private fun isCacheExpired(lastUpdated: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        val cacheLifetime = TimeUnit.HOURS.toMillis(1) // Cache expires after 1 hour
        return currentTime - lastUpdated > cacheLifetime
    }
}