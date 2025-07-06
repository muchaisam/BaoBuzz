package com.msdc.baobuzz.core.cache

import com.msdc.baobuzz.core.models.Fixture
import com.msdc.baobuzz.core.models.LeagueInsight
import com.msdc.baobuzz.core.models.LeagueStanding
import com.msdc.baobuzz.core.models.LiveMatch
import com.msdc.baobuzz.core.models.PlayerStat
import com.msdc.baobuzz.core.models.RecentResult
import com.msdc.baobuzz.core.models.Transfer
import com.msdc.baobuzz.core.models.UpcomingFixture
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FootballDataCache @Inject constructor() {

    private val cacheMutex = Mutex()
    private val cacheExpiryTime = 5 * 60 * 1000L // 5 minutes in milliseconds

    // Cache entries with timestamps
    private var liveMatchesCache: CacheEntry<List<LiveMatch>>? = null
    private var transfersCache: CacheEntry<List<Transfer>>? = null
    private var standingsCache: MutableMap<Int, CacheEntry<LeagueStanding>> = mutableMapOf()
    private var fixturesCache: MutableMap<String, CacheEntry<List<Fixture>>> = mutableMapOf()
    private var topScorersCache: MutableMap<Int, CacheEntry<List<PlayerStat>>> = mutableMapOf()
    private var upcomingFixturesCache: CacheEntry<List<UpcomingFixture>>? = null
    private var recentResultsCache: CacheEntry<List<RecentResult>>? = null
    private var leagueInsightsCache: CacheEntry<List<LeagueInsight>>? = null

    data class CacheEntry<T>(val data: T, val timestamp: Long) {
        fun isExpired(expiryTime: Long): Boolean {
            return System.currentTimeMillis() - timestamp > expiryTime
        }
    }

    suspend fun getLiveMatches(): List<LiveMatch>? =
        cacheMutex.withLock {
            liveMatchesCache
                ?.takeIf { !it.isExpired(2 * 60 * 1000L) }
                ?.data // 2 min for live matches
        }

    suspend fun cacheLiveMatches(data: List<LiveMatch>) =
        cacheMutex.withLock { liveMatchesCache = CacheEntry(data, System.currentTimeMillis()) }

    suspend fun getTransfers(): List<Transfer>? =
        cacheMutex.withLock { transfersCache?.takeIf { !it.isExpired(cacheExpiryTime) }?.data }

    suspend fun cacheTransfers(data: List<Transfer>) =
        cacheMutex.withLock { transfersCache = CacheEntry(data, System.currentTimeMillis()) }

    suspend fun getStandings(leagueId: Int): LeagueStanding? =
        cacheMutex.withLock {
            standingsCache[leagueId]?.takeIf { !it.isExpired(cacheExpiryTime) }?.data
        }

    suspend fun cacheStandings(leagueId: Int, data: LeagueStanding) =
        cacheMutex.withLock {
            standingsCache[leagueId] = CacheEntry(data, System.currentTimeMillis())
        }

    suspend fun getFixtures(key: String): List<Fixture>? =
        cacheMutex.withLock {
            fixturesCache[key]?.takeIf { !it.isExpired(cacheExpiryTime) }?.data
        }

    suspend fun cacheFixtures(key: String, data: List<Fixture>) =
        cacheMutex.withLock {
            fixturesCache[key] = CacheEntry(data, System.currentTimeMillis())
        }

    suspend fun getTopScorers(leagueId: Int): List<PlayerStat>? =
        cacheMutex.withLock {
            topScorersCache[leagueId]?.takeIf { !it.isExpired(cacheExpiryTime) }?.data
        }

    suspend fun cacheTopScorers(leagueId: Int, data: List<PlayerStat>) =
        cacheMutex.withLock {
            topScorersCache[leagueId] = CacheEntry(data, System.currentTimeMillis())
        }

    suspend fun getUpcomingFixtures(): List<UpcomingFixture>? =
        cacheMutex.withLock {
            upcomingFixturesCache?.takeIf { !it.isExpired(cacheExpiryTime) }?.data
        }

    suspend fun cacheUpcomingFixtures(data: List<UpcomingFixture>) =
        cacheMutex.withLock {
            upcomingFixturesCache = CacheEntry(data, System.currentTimeMillis())
        }

    suspend fun getRecentResults(): List<RecentResult>? =
        cacheMutex.withLock {
            recentResultsCache?.takeIf { !it.isExpired(cacheExpiryTime) }?.data
        }

    suspend fun cacheRecentResults(data: List<RecentResult>) =
        cacheMutex.withLock {
            recentResultsCache = CacheEntry(data, System.currentTimeMillis())
        }

    suspend fun getLeagueInsights(): List<LeagueInsight>? =
        cacheMutex.withLock {
            leagueInsightsCache?.takeIf { !it.isExpired(cacheExpiryTime) }?.data
        }

    suspend fun cacheLeagueInsights(data: List<LeagueInsight>) =
        cacheMutex.withLock {
            leagueInsightsCache = CacheEntry(data, System.currentTimeMillis())
        }

    suspend fun clearAllCache() =
        cacheMutex.withLock {
            liveMatchesCache = null
            transfersCache = null
            standingsCache.clear()
            fixturesCache.clear()
            topScorersCache.clear()
            upcomingFixturesCache = null
            recentResultsCache = null
            leagueInsightsCache = null
        }

    suspend fun clearExpiredEntries() =
        cacheMutex.withLock {
            val currentTime = System.currentTimeMillis()

            // Clean expired standings
            standingsCache.entries.removeAll { it.value.isExpired(cacheExpiryTime) }

            // Clean expired fixtures
            fixturesCache.entries.removeAll { it.value.isExpired(cacheExpiryTime) }

            // Clean expired top scorers
            topScorersCache.entries.removeAll { it.value.isExpired(cacheExpiryTime) }

            // Clear expired single entries
            if (liveMatchesCache?.isExpired(2 * 60 * 1000L) == true) {
                liveMatchesCache = null
            }
            if (transfersCache?.isExpired(cacheExpiryTime) == true) {
                transfersCache = null
            }
            if (upcomingFixturesCache?.isExpired(cacheExpiryTime) == true) {
                upcomingFixturesCache = null
            }
            if (recentResultsCache?.isExpired(cacheExpiryTime) == true) {
                recentResultsCache = null
            }
            if (leagueInsightsCache?.isExpired(cacheExpiryTime) == true) {
                leagueInsightsCache = null
            }
        }
}
