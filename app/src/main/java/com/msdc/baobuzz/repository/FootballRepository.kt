package com.msdc.baobuzz.repository

import com.msdc.baobuzz.interfaces.FootballApi
import com.msdc.baobuzz.models.Fixture
import com.msdc.baobuzz.daos.AppDatabase
import com.msdc.baobuzz.daos.CachedFixture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import com.msdc.baobuzz.interfaces.Result
import kotlinx.datetime.toInstant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.util.TimeZone


class FootballRepository(private val api: FootballApi, private val db: AppDatabase) {
    private val fixtureDao = db.fixtureDao()
    private val json = Json { ignoreUnknownKeys = true }
    private val cacheTimeout = 24 * 60 * 60 * 1000 // 24 hours in milliseconds


    suspend fun getUpcomingFixtures(leagueId: Int, next: Int = 10): Result<List<Fixture>> {
        return withContext(Dispatchers.IO) {
            try {
                val cachedFixtures = fixtureDao.getFixturesForLeague(leagueId)
                if (cachedFixtures.isNotEmpty() && !isCacheExpired(cachedFixtures)) {
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

    suspend fun fetchAndCacheFixtures(leagueId: Int, next: Int): List<Fixture> {
        val response = api.getUpcomingFixtures(leagueId, next, TimeZone.getDefault().id)
        val fixtures = response.response.filter { it.fixture.status.short == "NS" }

        fixtureDao.deleteFixturesForLeague(leagueId)
        fixtureDao.insertFixtures(fixtures.map {
            CachedFixture(
                id = it.fixture.id,
                leagueId = leagueId,
                fixtureData = json.encodeToString(it),
                fixtureDate = it.fixture.date.toInstant().toEpochMilliseconds(),
                lastUpdated = System.currentTimeMillis()
            )
        })

        return fixtures
    }

    private fun isCacheExpired(cachedFixtures: List<CachedFixture>): Boolean {
        val currentTime = System.currentTimeMillis()
        val nextFixtureTime = cachedFixtures
            .map { json.decodeFromString<Fixture>(it.fixtureData) }
            .minByOrNull { it.fixture.timestamp }
            ?.fixture
            ?.timestamp
            ?: return true

        val timeUntilNextFixture = nextFixtureTime - currentTime
        val cacheLifetime = when {
            timeUntilNextFixture < TimeUnit.HOURS.toMillis(6) -> TimeUnit.MINUTES.toMillis(30)
            timeUntilNextFixture < TimeUnit.DAYS.toMillis(1) -> TimeUnit.HOURS.toMillis(2)
            else -> TimeUnit.HOURS.toMillis(6)
        }

        return currentTime - cachedFixtures.first().lastUpdated > cacheLifetime
    }



    private fun isCacheExpired(timestamp: Long): Boolean {
        return System.currentTimeMillis() - timestamp > cacheTimeout
    }
}

