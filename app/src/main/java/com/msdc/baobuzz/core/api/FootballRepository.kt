package com.msdc.baobuzz.core.api

import com.msdc.baobuzz.core.cache.FootballDataCache
import com.msdc.baobuzz.core.models.Fixture
import com.msdc.baobuzz.core.models.LeagueInsight
import com.msdc.baobuzz.core.models.LeagueStanding
import com.msdc.baobuzz.core.models.LiveMatch
import com.msdc.baobuzz.core.models.PlayerDetails
import com.msdc.baobuzz.core.models.PlayerStat
import com.msdc.baobuzz.core.models.RecentResult
import com.msdc.baobuzz.core.models.SeasonSummary
import com.msdc.baobuzz.core.models.TeamDetails
import com.msdc.baobuzz.core.models.TransferDetails
import com.msdc.baobuzz.core.models.UpcomingFixture
import com.msdc.baobuzz.interfaces.FootballApi
import com.msdc.baobuzz.models.ApiTransfer
import com.msdc.baobuzz.models.Team
import com.msdc.baobuzz.models.TransferDetail
import com.msdc.baobuzz.models.footballdata.toDomainFixture
import com.msdc.baobuzz.models.footballdata.toDomainPlayerStat
import com.msdc.baobuzz.models.footballdata.toDomainTeam
import com.msdc.baobuzz.models.footballdata.toDomainTeamStanding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import com.msdc.baobuzz.models.Fixture as ApiFixture

interface FootballRepository {
    suspend fun getLiveMatches(leagueIds: List<Int>): List<LiveMatch>
    suspend fun getRecentTransfers(leagueIds: List<Int>): List<TransferDetails>
    suspend fun getLeagueStandings(leagueId: Int): LeagueStanding?
    suspend fun getFixtures(leagueId: Int, from: String, to: String): List<Fixture>
    suspend fun getPlayerStats(leagueId: Int): List<PlayerStat>
    suspend fun getTopScorers(leagueId: Int): List<PlayerStat>
    suspend fun getTopAssisters(leagueId: Int): List<PlayerStat>

    suspend fun getUpcomingFixtures(leagueIds: List<Int>, limit: Int = 10): List<UpcomingFixture>
    suspend fun getRecentResults(leagueIds: List<Int>, limit: Int = 10): List<RecentResult>
    suspend fun getSeasonSummary(leagueId: Int, season: Int? = null): SeasonSummary?
    suspend fun getLeagueInsights(leagueIds: List<Int>): List<LeagueInsight>
    suspend fun getTransfersByTeam(teamId: Int): List<TransferDetails>
}

@Singleton
class FootballRepositoryImpl
@Inject
constructor(
    private val footballApi: FootballApi,
    @Named("football-data") private val footballDataApi: com.msdc.baobuzz.core.api.interfaces.FootballDataApi,
    private val apiRequestTracker: ApiRequestTracker,
    private val cache: FootballDataCache
) : FootballRepository {

    private fun getCompetitionCode(leagueId: Int): String? {
        return when (leagueId) {
            39 -> "PL"
            78 -> "BL1"
            140 -> "PD"
            135 -> "SA"
            61 -> "FL1"
            else -> null
        }
    }

    override suspend fun getLiveMatches(leagueIds: List<Int>): List<LiveMatch> =
        withContext(Dispatchers.IO) {
            try {
                // Check cache first
                cache.getLiveMatches()?.let { cachedMatches ->
                    return@withContext cachedMatches.filter { match ->
                        leagueIds.any { leagueId ->
                            match.leagueId == leagueId
                        }
                    }
                }

                if (!apiRequestTracker.canMakeRequest()) {
                    // Offline fallback: return expired cache if available
                    Timber.w("Rate limit reached, attempting expired cache for live matches")
                    return@withContext cache.getLiveMatches(ignoreExpiry = true) ?: emptyList()
                }

                val matches = mutableListOf<LiveMatch>()

                leagueIds.forEach { leagueId ->
                    try {
                        val response = footballApi.getFixtures(
                            league = leagueId,
                            season = getCurrentSeason(),
                            live = "all"
                        )

                        response.response?.forEach { fixtureResponse: ApiFixture ->
                            matches.add(
                                LiveMatch(
                                    id = fixtureResponse.fixture.id.toString(),
                                    homeTeam = Team(
                                        id = fixtureResponse.teams.home.id,
                                        name = fixtureResponse.teams.home.name,
                                        code = null,
                                        country = "",
                                        founded = null,
                                        national = false,
                                        logo = fixtureResponse.teams.home.logo
                                    ),
                                    awayTeam = Team(
                                        id = fixtureResponse.teams.away.id,
                                        name = fixtureResponse.teams.away.name,
                                        code = null,
                                        country = "",
                                        founded = null,
                                        national = false,
                                        logo = fixtureResponse.teams.away.logo
                                    ),
                                    homeScore = fixtureResponse.goals.home,
                                    awayScore = fixtureResponse.goals.away,
                                    status = fixtureResponse.fixture.status.long,
                                    minute = fixtureResponse.fixture.status.elapsed,
                                    leagueId = leagueId
                                )
                            )
                        }

                        apiRequestTracker.recordRequest()
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to fetch live matches for league %d", leagueId)
                    }
                }

                cache.cacheLiveMatches(matches)
                matches
            } catch (e: Exception) {
                Timber.e(e, "Failed to fetch live matches")
                emptyList()
            }
        }

    override suspend fun getRecentTransfers(leagueIds: List<Int>): List<TransferDetails> =
        withContext(Dispatchers.IO) {
            try {
                cache.getTransfers()?.let { cachedTransfers ->
                    return@withContext cachedTransfers
                }

                if (!apiRequestTracker.canMakeRequest()) {
                    Timber.w("Rate limit reached, attempting expired cache for transfers")
                    return@withContext cache.getTransfers(ignoreExpiry = true) ?: emptyList()
                }

                val transfers = mutableListOf<TransferDetails>()

                leagueIds.forEach { leagueId ->
                    try {
                        val response = footballApi.getTransfers(team = null)

                        response.response?.take(10)?.forEach { transferResponse: ApiTransfer ->
                            transferResponse.transfers.forEach { transferDetail ->
                                transfers.add(
                                    convertToTransferDetails(transferResponse, transferDetail)
                                )
                            }
                        }

                        apiRequestTracker.recordRequest()
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to fetch transfers for league %d", leagueId)
                    }
                }

                val distinctTransfers =
                    transfers.distinctBy { "${it.player.id}_${it.date}" }
                        .sortedByDescending { it.date }

                cache.cacheTransfers(distinctTransfers)
                distinctTransfers
            } catch (e: Exception) {
                Timber.e(e, "Failed to fetch recent transfers")
                emptyList()
            }
        }

    override suspend fun getLeagueStandings(leagueId: Int): LeagueStanding? =
        withContext(Dispatchers.IO) {
            try {
                cache.getStandings(leagueId)?.let { cachedStanding ->
                    return@withContext cachedStanding
                }

                if (!apiRequestTracker.canMakeRequest()) {
                    Timber.w("Rate limit reached, attempting expired cache for standings")
                    return@withContext cache.getStandings(leagueId, ignoreExpiry = true)
                }

                val competitionCode = getCompetitionCode(leagueId)
                    ?: return@withContext null

                val response = footballDataApi.getStandings(competitionCode)
                val standingsTable = response.standings.firstOrNull()?.table
                val competition = response.competition

                if (standingsTable != null) {
                    apiRequestTracker.recordRequest()

                    val leagueStanding = LeagueStanding(
                        leagueId = leagueId,
                        leagueName = competition.name,
                        leagueLogo = competition.emblem ?: "",
                        teams = standingsTable.map { it.toDomainTeamStanding() }
                    )

                    cache.cacheStandings(leagueId, leagueStanding)
                    leagueStanding
                } else {
                    null
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to fetch standings for league %d", leagueId)
                null
            }
        }

    override suspend fun getFixtures(
        leagueId: Int,
        from: String,
        to: String
    ): List<Fixture> = withContext(Dispatchers.IO) {
        try {
            val cacheKey = "${leagueId}_${from}_${to}"

            cache.getFixtures(cacheKey)?.let { cachedFixtures ->
                return@withContext cachedFixtures
            }

            if (!apiRequestTracker.canMakeRequest()) {
                return@withContext cache.getFixtures(cacheKey, ignoreExpiry = true) ?: emptyList()
            }

            val competitionCode = getCompetitionCode(leagueId)
                ?: return@withContext emptyList()

            val response = footballDataApi.getMatches(
                competitionCode = competitionCode,
                dateFrom = from,
                dateTo = to
            )

            val fixtures = response.matches.map { it.toDomainFixture() }
            apiRequestTracker.recordRequest()

            cache.cacheFixtures(cacheKey, fixtures)
            fixtures
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch fixtures for league %d", leagueId)
            emptyList()
        }
    }

    override suspend fun getPlayerStats(leagueId: Int): List<PlayerStat> =
        withContext(Dispatchers.IO) {
            try {
                cache.getTopScorers(leagueId)?.let { cachedStats ->
                    return@withContext cachedStats
                }

                if (!apiRequestTracker.canMakeRequest()) {
                    return@withContext cache.getTopScorers(leagueId, ignoreExpiry = true)
                        ?: emptyList()
                }

                val competitionCode = getCompetitionCode(leagueId)
                    ?: return@withContext emptyList()

                val response = footballDataApi.getTopScorers(
                    competitionCode = competitionCode,
                    limit = 20
                )

                val players = response.scorers.map { it.toDomainPlayerStat() }
                apiRequestTracker.recordRequest()

                cache.cacheTopScorers(leagueId, players)
                players
            } catch (e: Exception) {
                Timber.e(e, "Failed to fetch player stats for league %d", leagueId)
                emptyList()
            }
        }

    override suspend fun getTopScorers(leagueId: Int): List<PlayerStat> {
        return getPlayerStats(leagueId).sortedByDescending { it.goals }
    }

    override suspend fun getTopAssisters(leagueId: Int): List<PlayerStat> {
        return getPlayerStats(leagueId).sortedByDescending { it.assists }
    }

    override suspend fun getUpcomingFixtures(
        leagueIds: List<Int>,
        limit: Int
    ): List<UpcomingFixture> = withContext(Dispatchers.IO) {
        try {
            cache.getUpcomingFixtures()?.let { cachedFixtures ->
                return@withContext cachedFixtures
                    .filter { fixture -> leagueIds.contains(fixture.leagueId) }
                    .take(limit)
            }

            if (!apiRequestTracker.canMakeRequest()) {
                return@withContext cache.getUpcomingFixtures(ignoreExpiry = true)
                    ?.filter { leagueIds.contains(it.leagueId) }
                    ?.take(limit)
                    ?: emptyList()
            }

            val today = java.time.LocalDate.now().toString()
            val futureDate = java.time.LocalDate.now().plusDays(30).toString()

            val fixtures = supervisorScope {
                leagueIds.map { leagueId ->
                    async {
                        try {
                            val competitionCode = getCompetitionCode(leagueId) ?: return@async emptyList()
                            val response = footballDataApi.getMatches(
                                competitionCode = competitionCode,
                                status = "SCHEDULED",
                                dateFrom = today,
                                dateTo = futureDate
                            )
                            apiRequestTracker.recordRequest()

                            response.matches.take(limit / leagueIds.size + 1).map { match ->
                                UpcomingFixture(
                                    id = match.id.toString(),
                                    homeTeam = match.homeTeam.toDomainTeam(),
                                    awayTeam = match.awayTeam.toDomainTeam(),
                                    dateTime = match.utcDate,
                                    venue = "TBD",
                                    round = "Matchday ${match.matchday}",
                                    leagueId = leagueId,
                                    leagueName = match.competition?.name ?: "Unknown"
                                )
                            }
                        } catch (e: Exception) {
                            Timber.e(e, "Failed to fetch upcoming fixtures for league %d", leagueId)
                            emptyList()
                        }
                    }
                }.awaitAll().flatten()
            }

            val sortedFixtures = fixtures.sortedBy { it.dateTime }.take(limit)
            cache.cacheUpcomingFixtures(sortedFixtures)
            sortedFixtures
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch upcoming fixtures")
            emptyList()
        }
    }

    override suspend fun getRecentResults(leagueIds: List<Int>, limit: Int): List<RecentResult> =
        withContext(Dispatchers.IO) {
            try {
                cache.getRecentResults()?.let { cachedResults ->
                    return@withContext cachedResults
                        .filter { result -> leagueIds.contains(result.leagueId) }
                        .take(limit)
                }

                if (!apiRequestTracker.canMakeRequest()) {
                    return@withContext cache.getRecentResults(ignoreExpiry = true)
                        ?.filter { leagueIds.contains(it.leagueId) }
                        ?.take(limit)
                        ?: emptyList()
                }

                val pastDate = java.time.LocalDate.now().minusDays(30).toString()
                val today = java.time.LocalDate.now().toString()

                val results = supervisorScope {
                    leagueIds.map { leagueId ->
                        async {
                            try {
                                val competitionCode = getCompetitionCode(leagueId) ?: return@async emptyList()
                                val response = footballDataApi.getMatches(
                                    competitionCode = competitionCode,
                                    status = "FINISHED",
                                    dateFrom = pastDate,
                                    dateTo = today
                                )
                                apiRequestTracker.recordRequest()

                                response.matches.take(limit / leagueIds.size + 1).map { match ->
                                    RecentResult(
                                        id = match.id.toString(),
                                        homeTeam = match.homeTeam.toDomainTeam(),
                                        awayTeam = match.awayTeam.toDomainTeam(),
                                        homeScore = match.score.fullTime?.home ?: 0,
                                        awayScore = match.score.fullTime?.away ?: 0,
                                        date = match.utcDate,
                                        round = "Matchday ${match.matchday}",
                                        leagueId = leagueId,
                                        leagueName = match.competition?.name ?: "Unknown"
                                    )
                                }
                            } catch (e: Exception) {
                                Timber.e(e, "Failed to fetch recent results for league %d", leagueId)
                                emptyList()
                            }
                        }
                    }.awaitAll().flatten()
                }

                val sortedResults = results.sortedByDescending { it.date }.take(limit)
                cache.cacheRecentResults(sortedResults)
                sortedResults
            } catch (e: Exception) {
                Timber.e(e, "Failed to fetch recent results")
                emptyList()
            }
        }

    override suspend fun getSeasonSummary(leagueId: Int, season: Int?): SeasonSummary? =
        withContext(Dispatchers.IO) {
            try {
                if (!apiRequestTracker.canMakeRequest()) {
                    return@withContext null
                }

                val competitionCode = getCompetitionCode(leagueId)
                    ?: return@withContext null

                val standingsResponse = footballDataApi.getStandings(competitionCode)
                val table = standingsResponse.standings.firstOrNull()?.table
                val champion = table?.firstOrNull()
                val competition = standingsResponse.competition

                if (table != null) {
                    val topScorersList = getTopScorers(leagueId)
                    val topScorer = topScorersList.firstOrNull()
                    apiRequestTracker.recordRequest()

                    SeasonSummary(
                        leagueId = leagueId,
                        leagueName = competition.name,
                        leagueLogo = competition.emblem ?: "",
                        season = getCurrentSeason(),
                        champion = champion?.let { it.team.toDomainTeam() },
                        topScorer = topScorer,
                        totalGoals = table.sumOf { it.goalsFor },
                        totalMatches = table.sumOf { it.playedGames },
                        isCurrentSeason = true
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to fetch season summary for league %d", leagueId)
                null
            }
        }

    override suspend fun getLeagueInsights(leagueIds: List<Int>): List<LeagueInsight> =
        withContext(Dispatchers.IO) {
            try {
                cache.getLeagueInsights()?.let { cachedInsights ->
                    return@withContext cachedInsights.filter { insight ->
                        leagueIds.contains(insight.leagueId)
                    }
                }

                // Parallelize per-league insight fetching
                val insights = supervisorScope {
                    leagueIds.map { leagueId ->
                        async {
                            try {
                                val standings = getLeagueStandings(leagueId)
                                    ?: return@async null
                                val upcomingFixtures = getUpcomingFixtures(listOf(leagueId), 1)
                                val recentResults = getRecentResults(listOf(leagueId), 1)
                                val topScorers = getTopScorers(leagueId)

                                LeagueInsight(
                                    leagueId = leagueId,
                                    leagueName = standings.leagueName,
                                    leagueLogo = standings.leagueLogo,
                                    currentStanding = standings.teams.firstOrNull(),
                                    nextFixture = upcomingFixtures.firstOrNull(),
                                    lastResult = recentResults.firstOrNull(),
                                    topScorer = topScorers.firstOrNull(),
                                    matchesPlayed = standings.teams.firstOrNull()?.played ?: 0,
                                    matchesRemaining = 38 - (standings.teams.firstOrNull()?.played ?: 0)
                                )
                            } catch (e: Exception) {
                                Timber.e(e, "Failed to fetch insights for league %d", leagueId)
                                null
                            }
                        }
                    }.awaitAll().filterNotNull()
                }

                cache.cacheLeagueInsights(insights)
                insights
            } catch (e: Exception) {
                Timber.e(e, "Failed to fetch league insights")
                emptyList()
            }
        }

    override suspend fun getTransfersByTeam(teamId: Int): List<TransferDetails> =
        withContext(Dispatchers.IO) {
            try {
                cache.getTransfers(teamId.toString())?.let { cachedTransfers ->
                    return@withContext cachedTransfers
                }

                if (!apiRequestTracker.canMakeRequest()) {
                    return@withContext cache.getTransfers(teamId.toString(), ignoreExpiry = true)
                        ?: emptyList()
                }

                val response = footballApi.getTransfersByTeam(team = teamId)

                val transfers = response.response?.flatMap { transferResponse ->
                    transferResponse.transfers.map { transferDetail ->
                        convertToTransferDetails(transferResponse, transferDetail)
                    }
                } ?: emptyList()

                apiRequestTracker.recordRequest()
                cache.cacheTransfers(teamId.toString(), transfers)
                transfers
            } catch (e: Exception) {
                Timber.e(e, "Failed to fetch transfers for team %d", teamId)
                emptyList()
            }
        }

    private fun convertToTransferDetails(
        transferResponse: ApiTransfer,
        transferDetail: TransferDetail
    ): TransferDetails {
        return TransferDetails(
            date = transferDetail.date,
            type = transferDetail.type,
            teamIn = TeamDetails(
                id = transferDetail.teams.`in`.id,
                name = transferDetail.teams.`in`.name,
                logo = transferDetail.teams.`in`.logo
            ),
            teamOut = TeamDetails(
                id = transferDetail.teams.out.id,
                name = transferDetail.teams.out.name,
                logo = transferDetail.teams.out.logo
            ),
            player = PlayerDetails(
                id = transferResponse.player.id,
                name = transferResponse.player.name
            )
        )
    }

    private fun getCurrentSeason(): Int {
        val now = java.time.LocalDate.now()
        // Football seasons typically start in August
        return if (now.monthValue >= 8) now.year else now.year - 1
    }
}
