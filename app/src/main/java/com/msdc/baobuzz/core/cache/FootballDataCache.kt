package com.msdc.baobuzz.core.cache

import com.msdc.baobuzz.core.database.dao.CacheDao
import com.msdc.baobuzz.core.database.entities.CacheEntity
import com.msdc.baobuzz.core.models.Fixture
import com.msdc.baobuzz.core.models.LeagueInsight
import com.msdc.baobuzz.core.models.LeagueStanding
import com.msdc.baobuzz.core.models.LiveMatch
import com.msdc.baobuzz.core.models.PlayerStat
import com.msdc.baobuzz.core.models.RecentResult
import com.msdc.baobuzz.core.models.TransferDetails
import com.msdc.baobuzz.core.models.UpcomingFixture
import com.msdc.baobuzz.core.utils.JsonSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FootballDataCache @Inject constructor(
    private val cacheDao: CacheDao,
    private val jsonSerializer: JsonSerializer
) {

    // Cache expiry times in milliseconds
    private val liveMatchesExpiryTime = 2 * 60 * 1000L // 2 minutes for live matches
    private val standardExpiryTime = 5 * 60 * 1000L // 5 minutes for most data
    private val longExpiryTime = 30 * 60 * 1000L // 30 minutes for standings/stats
    private val veryLongExpiryTime = 2 * 60 * 60 * 1000L // 2 hours for transfers

    // Cache data type constants
    private companion object {
        const val LIVE_MATCHES = "live_matches"
        const val TRANSFERS = "transfers"
        const val STANDINGS = "standings"
        const val FIXTURES = "fixtures"
        const val TOP_SCORERS = "top_scorers"
        const val UPCOMING_FIXTURES = "upcoming_fixtures"
        const val RECENT_RESULTS = "recent_results"
        const val LEAGUE_INSIGHTS = "league_insights"
    }

    private suspend fun getEntry(key: String, ignoreExpiry: Boolean): CacheEntity? {
        return if (ignoreExpiry) {
            cacheDao.getCacheEntryIgnoringExpiry(key)
        } else {
            cacheDao.getCacheEntry(key)
        }
    }

    // Live matches
    suspend fun getLiveMatches(ignoreExpiry: Boolean = false): List<LiveMatch>? =
        withContext(Dispatchers.IO) {
            getEntry(LIVE_MATCHES, ignoreExpiry)?.let { cacheEntry ->
                jsonSerializer.deserializeList<LiveMatch>(cacheEntry.jsonData)
            }
        }

    suspend fun cacheLiveMatches(data: List<LiveMatch>) = withContext(Dispatchers.IO) {
        val cacheEntry = CacheEntity(
            cacheKey = LIVE_MATCHES,
            dataType = LIVE_MATCHES,
            jsonData = jsonSerializer.serialize(data),
            lastUpdated = System.currentTimeMillis(),
            expiryTime = liveMatchesExpiryTime
        )
        cacheDao.insertCacheEntry(cacheEntry)
    }

    // Transfers
    suspend fun getTransfers(ignoreExpiry: Boolean = false): List<TransferDetails>? =
        withContext(Dispatchers.IO) {
            getEntry(TRANSFERS, ignoreExpiry)?.let { cacheEntry ->
                jsonSerializer.deserializeList<TransferDetails>(cacheEntry.jsonData)
            }
        }

    suspend fun cacheTransfers(data: List<TransferDetails>) = withContext(Dispatchers.IO) {
        val cacheEntry = CacheEntity(
            cacheKey = TRANSFERS,
            dataType = TRANSFERS,
            jsonData = jsonSerializer.serialize(data),
            lastUpdated = System.currentTimeMillis(),
            expiryTime = veryLongExpiryTime
        )
        cacheDao.insertCacheEntry(cacheEntry)
    }

    // Transfers for specific team
    suspend fun getTransfers(teamId: String, ignoreExpiry: Boolean = false): List<TransferDetails>? =
        withContext(Dispatchers.IO) {
            val cacheKey = "${TRANSFERS}_$teamId"
            getEntry(cacheKey, ignoreExpiry)?.let { cacheEntry ->
                jsonSerializer.deserializeList<TransferDetails>(cacheEntry.jsonData)
            }
        }

    suspend fun cacheTransfers(teamId: String, data: List<TransferDetails>) =
        withContext(Dispatchers.IO) {
            val cacheKey = "${TRANSFERS}_$teamId"
            val cacheEntry = CacheEntity(
                cacheKey = cacheKey,
                dataType = TRANSFERS,
                jsonData = jsonSerializer.serialize(data),
                lastUpdated = System.currentTimeMillis(),
                expiryTime = veryLongExpiryTime
            )
            cacheDao.insertCacheEntry(cacheEntry)
        }

    // League standings
    suspend fun getStandings(leagueId: Int, ignoreExpiry: Boolean = false): LeagueStanding? =
        withContext(Dispatchers.IO) {
            val cacheKey = "${STANDINGS}_$leagueId"
            getEntry(cacheKey, ignoreExpiry)?.let { cacheEntry ->
                jsonSerializer.deserialize<LeagueStanding>(cacheEntry.jsonData)
            }
        }

    suspend fun cacheStandings(leagueId: Int, data: LeagueStanding) = withContext(Dispatchers.IO) {
        val cacheKey = "${STANDINGS}_$leagueId"
        val cacheEntry = CacheEntity(
            cacheKey = cacheKey,
            dataType = STANDINGS,
            jsonData = jsonSerializer.serialize(data),
            lastUpdated = System.currentTimeMillis(),
            expiryTime = longExpiryTime,
            leagueIds = leagueId.toString()
        )
        cacheDao.insertCacheEntry(cacheEntry)
    }

    // Fixtures
    suspend fun getFixtures(key: String, ignoreExpiry: Boolean = false): List<Fixture>? =
        withContext(Dispatchers.IO) {
            val cacheKey = "${FIXTURES}_$key"
            getEntry(cacheKey, ignoreExpiry)?.let { cacheEntry ->
                jsonSerializer.deserializeList<Fixture>(cacheEntry.jsonData)
            }
        }

    suspend fun cacheFixtures(key: String, data: List<Fixture>) = withContext(Dispatchers.IO) {
        val cacheKey = "${FIXTURES}_$key"
        val cacheEntry = CacheEntity(
            cacheKey = cacheKey,
            dataType = FIXTURES,
            jsonData = jsonSerializer.serialize(data),
            lastUpdated = System.currentTimeMillis(),
            expiryTime = standardExpiryTime
        )
        cacheDao.insertCacheEntry(cacheEntry)
    }

    // Top scorers
    suspend fun getTopScorers(leagueId: Int, ignoreExpiry: Boolean = false): List<PlayerStat>? =
        withContext(Dispatchers.IO) {
            val cacheKey = "${TOP_SCORERS}_$leagueId"
            getEntry(cacheKey, ignoreExpiry)?.let { cacheEntry ->
                jsonSerializer.deserializeList<PlayerStat>(cacheEntry.jsonData)
            }
        }

    suspend fun cacheTopScorers(leagueId: Int, data: List<PlayerStat>) =
        withContext(Dispatchers.IO) {
            val cacheKey = "${TOP_SCORERS}_$leagueId"
            val cacheEntry = CacheEntity(
                cacheKey = cacheKey,
                dataType = TOP_SCORERS,
                jsonData = jsonSerializer.serialize(data),
                lastUpdated = System.currentTimeMillis(),
                expiryTime = longExpiryTime,
                leagueIds = leagueId.toString()
            )
            cacheDao.insertCacheEntry(cacheEntry)
        }

    // Upcoming fixtures
    suspend fun getUpcomingFixtures(ignoreExpiry: Boolean = false): List<UpcomingFixture>? =
        withContext(Dispatchers.IO) {
            getEntry(UPCOMING_FIXTURES, ignoreExpiry)?.let { cacheEntry ->
                jsonSerializer.deserializeList<UpcomingFixture>(cacheEntry.jsonData)
            }
        }

    suspend fun cacheUpcomingFixtures(data: List<UpcomingFixture>) = withContext(Dispatchers.IO) {
        val cacheEntry = CacheEntity(
            cacheKey = UPCOMING_FIXTURES,
            dataType = UPCOMING_FIXTURES,
            jsonData = jsonSerializer.serialize(data),
            lastUpdated = System.currentTimeMillis(),
            expiryTime = standardExpiryTime
        )
        cacheDao.insertCacheEntry(cacheEntry)
    }

    // Recent results
    suspend fun getRecentResults(ignoreExpiry: Boolean = false): List<RecentResult>? =
        withContext(Dispatchers.IO) {
            getEntry(RECENT_RESULTS, ignoreExpiry)?.let { cacheEntry ->
                jsonSerializer.deserializeList<RecentResult>(cacheEntry.jsonData)
            }
        }

    suspend fun cacheRecentResults(data: List<RecentResult>) = withContext(Dispatchers.IO) {
        val cacheEntry = CacheEntity(
            cacheKey = RECENT_RESULTS,
            dataType = RECENT_RESULTS,
            jsonData = jsonSerializer.serialize(data),
            lastUpdated = System.currentTimeMillis(),
            expiryTime = standardExpiryTime
        )
        cacheDao.insertCacheEntry(cacheEntry)
    }

    // League insights
    suspend fun getLeagueInsights(ignoreExpiry: Boolean = false): List<LeagueInsight>? =
        withContext(Dispatchers.IO) {
            getEntry(LEAGUE_INSIGHTS, ignoreExpiry)?.let { cacheEntry ->
                jsonSerializer.deserializeList<LeagueInsight>(cacheEntry.jsonData)
            }
        }

    suspend fun cacheLeagueInsights(data: List<LeagueInsight>) = withContext(Dispatchers.IO) {
        val cacheEntry = CacheEntity(
            cacheKey = LEAGUE_INSIGHTS,
            dataType = LEAGUE_INSIGHTS,
            jsonData = jsonSerializer.serialize(data),
            lastUpdated = System.currentTimeMillis(),
            expiryTime = standardExpiryTime
        )
        cacheDao.insertCacheEntry(cacheEntry)
    }

    // Cache management
    suspend fun clearAllCache() = withContext(Dispatchers.IO) {
        cacheDao.clearAllCache()
    }

    suspend fun clearExpiredEntries() = withContext(Dispatchers.IO) {
        cacheDao.deleteExpiredEntries()
    }

    suspend fun getCacheSize(): Int = withContext(Dispatchers.IO) {
        cacheDao.getCacheSize()
    }

    suspend fun clearCacheByType(dataType: String) = withContext(Dispatchers.IO) {
        cacheDao.deleteCacheEntriesByType(dataType)
    }

    suspend fun clearCacheByLeague(leagueId: Int) = withContext(Dispatchers.IO) {
        val allEntries = cacheDao.getCacheEntriesByLeagueId(leagueId.toString())
        allEntries.forEach { entry ->
            cacheDao.deleteCacheEntry(entry.cacheKey)
        }
    }
}
