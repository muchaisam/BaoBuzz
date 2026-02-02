package com.msdc.baobuzz.core.api

import com.msdc.baobuzz.core.cache.FootballDataCache
import com.msdc.baobuzz.core.models.*
import com.msdc.baobuzz.interfaces.FootballApi
import com.msdc.baobuzz.models.ApiTransfer
import com.msdc.baobuzz.models.PlayerStatResponse
import com.msdc.baobuzz.models.Team
import com.msdc.baobuzz.models.TransferDetail
import com.msdc.baobuzz.models.footballdata.*
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

    // Enhanced methods for richer home screen content
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
    private val footballApi: FootballApi, // Keep for transfers (optional)
    @Named("football-data") private val footballDataApi: com.msdc.baobuzz.core.api.interfaces.FootballDataApi,
    private val apiRequestTracker: ApiRequestTracker,
    private val cache: FootballDataCache
) : FootballRepository {

    // Map league IDs to football-data.org competition codes
    private fun getCompetitionCode(leagueId: Int): String? {
        return when (leagueId) {
            39 -> "PL"    // Premier League
            78 -> "BL1"   // Bundesliga
            140 -> "PD"   // La Liga
            135 -> "SA"   // Serie A
            61 -> "FL1"   // Ligue 1
            else -> null
        }
    }

    override suspend fun getLiveMatches(leagueIds: List<Int>): List<LiveMatch> {
        return try {
            // Check cache first
            cache.getLiveMatches()?.let { cachedMatches ->
                return@getLiveMatches cachedMatches.filter { match ->
                    leagueIds.any { leagueId ->
                        match.homeTeam.id.toString().contains(leagueId.toString()) ||
                                match.awayTeam.id.toString().contains(leagueId.toString())
                    }
                }
            }

            if (!apiRequestTracker.canMakeRequest()) {
                return emptyList()
            }

            val matches = mutableListOf<LiveMatch>()

            leagueIds.forEach { leagueId ->
                val response =
                    footballApi.getFixtures(
                        league = leagueId,
                        season = getCurrentSeason(),
                        live = "all"
                    )

                response.response?.forEach { fixtureResponse: ApiFixture ->
                    matches.add(
                        LiveMatch(
                            id = fixtureResponse.fixture.id.toString(),
                            homeTeam =
                            Team(
                                id = fixtureResponse.teams.home.id,
                                name = fixtureResponse.teams.home.name,
                                code = null,
                                country = "",
                                founded = null,
                                national = false,
                                logo = fixtureResponse.teams.home.logo
                            ),
                            awayTeam =
                            Team(
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
                            minute = fixtureResponse.fixture.status.elapsed
                        )
                    )
                }

                apiRequestTracker.recordRequest()
            }

            // Cache the results
            cache.cacheLiveMatches(matches)
            matches
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getRecentTransfers(leagueIds: List<Int>): List<TransferDetails> {
        return try {
            // Check cache first
            cache.getTransfers()?.let { cachedTransfers ->
                return@getRecentTransfers cachedTransfers
            }

            if (!apiRequestTracker.canMakeRequest()) {
                return emptyList()
            }

            val transfers = mutableListOf<TransferDetails>()

            leagueIds.forEach { leagueId ->
                val response =
                    footballApi.getTransfers(
                        team = null // Get all transfers for the league
                    )

                response.response?.take(10)?.forEach { transferResponse: ApiTransfer ->
                    // For each transfer response, iterate through the transfer details
                    transferResponse.transfers.forEach { transferDetail ->
                        transfers.add(convertToTransferDetails(transferResponse, transferDetail))
                    }
                }

                apiRequestTracker.recordRequest()
            }

            val distinctTransfers =
                transfers.distinctBy { "${it.player.id}_${it.date}" }.sortedByDescending {
                    it.date
                }

            // Cache the results
            cache.cacheTransfers(distinctTransfers)
            distinctTransfers
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getLeagueStandings(leagueId: Int): LeagueStanding? {
        return try {
            // Check cache first
            cache.getStandings(leagueId)?.let { cachedStanding ->
                return@getLeagueStandings cachedStanding
            }

            if (!apiRequestTracker.canMakeRequest()) {
                return null
            }

            // Get competition code for football-data.org
            val competitionCode = getCompetitionCode(leagueId) ?: return null

            // Use new football-data.org API
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

                // Cache the result
                cache.cacheStandings(leagueId, leagueStanding)
                leagueStanding
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getFixtures(
        leagueId: Int,
        from: String,
        to: String
    ): List<com.msdc.baobuzz.core.models.Fixture> {
        return try {
            val cacheKey = "${leagueId}_${from}_${to}"

            // Check cache first
            cache.getFixtures(cacheKey)?.let { cachedFixtures ->
                return@getFixtures cachedFixtures
            }

            if (!apiRequestTracker.canMakeRequest()) {
                return emptyList()
            }

            // Get competition code for football-data.org
            val competitionCode = getCompetitionCode(leagueId) ?: return emptyList()

            // Use new football-data.org API
            val response = footballDataApi.getMatches(
                competitionCode = competitionCode,
                dateFrom = from,
                dateTo = to
            )

            val fixtures = response.matches.map { it.toDomainFixture() }

            apiRequestTracker.recordRequest()

            // Cache the results
            cache.cacheFixtures(cacheKey, fixtures)
            fixtures
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getPlayerStats(leagueId: Int): List<PlayerStat> {
        return try {
            // Check cache first
            cache.getTopScorers(leagueId)?.let { cachedStats ->
                return@getPlayerStats cachedStats
            }

            if (!apiRequestTracker.canMakeRequest()) {
                return emptyList()
            }

            // Get competition code for football-data.org
            val competitionCode = getCompetitionCode(leagueId) ?: return emptyList()

            // Use new football-data.org API
            val response = footballDataApi.getTopScorers(
                competitionCode = competitionCode,
                limit = 20
            )

            val players = response.scorers.map { it.toDomainPlayerStat() }

            apiRequestTracker.recordRequest()

            // Cache the results
            cache.cacheTopScorers(leagueId, players)
            players
        } catch (e: Exception) {
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
    ): List<UpcomingFixture> {
        return try {
            // Check cache first
            cache.getUpcomingFixtures()?.let { cachedFixtures ->
                return@getUpcomingFixtures cachedFixtures
                    .filter { fixture -> leagueIds.contains(fixture.leagueId) }
                    .take(limit)
            }

            if (!apiRequestTracker.canMakeRequest()) {
                return emptyList()
            }

            val fixtures = mutableListOf<UpcomingFixture>()
            val today = java.time.LocalDate.now().toString()
            val futureDate = java.time.LocalDate.now().plusDays(30).toString()

            leagueIds.forEach { leagueId ->
                val competitionCode = getCompetitionCode(leagueId) ?: return@forEach

                // Use new football-data.org API
                val response = footballDataApi.getMatches(
                    competitionCode = competitionCode,
                    status = "SCHEDULED",
                    dateFrom = today,
                    dateTo = futureDate
                )

                response.matches.take(limit / leagueIds.size + 1).forEach { match ->
                    fixtures.add(
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
                    )
                }

                apiRequestTracker.recordRequest()
            }

            val sortedFixtures = fixtures.sortedBy { it.dateTime }.take(limit)

            // Cache the results
            cache.cacheUpcomingFixtures(sortedFixtures)
            sortedFixtures
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getRecentResults(leagueIds: List<Int>, limit: Int): List<RecentResult> {
        return try {
            // Check cache first
            cache.getRecentResults()?.let { cachedResults ->
                return@getRecentResults cachedResults
                    .filter { result -> leagueIds.contains(result.leagueId) }
                    .take(limit)
            }

            if (!apiRequestTracker.canMakeRequest()) {
                return emptyList()
            }

            val results = mutableListOf<RecentResult>()
            val pastDate = java.time.LocalDate.now().minusDays(30).toString()
            val today = java.time.LocalDate.now().toString()

            leagueIds.forEach { leagueId ->
                val competitionCode = getCompetitionCode(leagueId) ?: return@forEach

                // Use new football-data.org API
                val response = footballDataApi.getMatches(
                    competitionCode = competitionCode,
                    status = "FINISHED",
                    dateFrom = pastDate,
                    dateTo = today
                )

                response.matches.take(limit / leagueIds.size + 1).forEach { match ->
                    results.add(
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
                    )
                }

                apiRequestTracker.recordRequest()
            }

            val sortedResults = results.sortedByDescending { it.date }.take(limit)

            // Cache the results
            cache.cacheRecentResults(sortedResults)
            sortedResults
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getSeasonSummary(leagueId: Int, season: Int?): SeasonSummary? {
        return try {
            if (!apiRequestTracker.canMakeRequest()) {
                return null
            }

            // Get competition code for football-data.org
            val competitionCode = getCompetitionCode(leagueId) ?: return null

            // Use new football-data.org API
            val standingsResponse = footballDataApi.getStandings(competitionCode)

            val table = standingsResponse.standings.firstOrNull()?.table
            val champion = table?.firstOrNull()
            val competition = standingsResponse.competition

            if (table != null) {
                // Get top scorer for the season
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
            null
        }
    }

    override suspend fun getLeagueInsights(leagueIds: List<Int>): List<LeagueInsight> {
        return try {
            // Check cache first
            cache.getLeagueInsights()?.let { cachedInsights ->
                return@getLeagueInsights cachedInsights.filter { insight ->
                    leagueIds.contains(insight.leagueId)
                }
            }

            val insights = mutableListOf<LeagueInsight>()

            leagueIds.forEach { leagueId ->
                val standings = getLeagueStandings(leagueId)
                val upcomingFixtures = getUpcomingFixtures(listOf(leagueId), 1)
                val recentResults = getRecentResults(listOf(leagueId), 1)
                val topScorers = getTopScorers(leagueId)

                if (standings != null) {
                    insights.add(
                        LeagueInsight(
                            leagueId = leagueId,
                            leagueName = standings.leagueName,
                            leagueLogo = standings.leagueLogo,
                            currentStanding = standings.teams.firstOrNull(),
                            nextFixture = upcomingFixtures.firstOrNull(),
                            lastResult = recentResults.firstOrNull(),
                            topScorer = topScorers.firstOrNull(),
                            matchesPlayed = standings.teams.firstOrNull()?.played ?: 0,
                            matchesRemaining =
                            38 -
                                    (standings.teams.firstOrNull()?.played
                                        ?: 0) // Assuming 38 match season
                        )
                    )
                }
            }

            // Cache the results
            cache.cacheLeagueInsights(insights)
            insights
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getTransfersByTeam(teamId: Int): List<TransferDetails> {
        return try {
            // Check cache first
            cache.getTransfers(teamId.toString())?.let { cachedTransfers ->
                return@getTransfersByTeam cachedTransfers
            }

            if (!apiRequestTracker.canMakeRequest()) {
                return emptyList()
            }

            val response = footballApi.getTransfersByTeam(team = teamId)

            val transfers =
                response.response?.flatMap { transferResponse ->
                    transferResponse.transfers.map { transferDetail ->
                        convertToTransferDetails(transferResponse, transferDetail)
                    }
                }
                    ?: emptyList()

            apiRequestTracker.recordRequest()

            // Cache the results
            cache.cacheTransfers(teamId.toString(), transfers)
            transfers
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Utility methods to convert between models
    private fun convertToTransferDetails(
        transferResponse: ApiTransfer,
        transferDetail: TransferDetail
    ): TransferDetails {
        return TransferDetails(
            date = transferDetail.date,
            type = transferDetail.type,
            teamIn =
            TeamDetails(
                id = transferDetail.teams.`in`.id,
                name = transferDetail.teams.`in`.name,
                logo = transferDetail.teams.`in`.logo
            ),
            teamOut =
            TeamDetails(
                id = transferDetail.teams.out.id,
                name = transferDetail.teams.out.name,
                logo = transferDetail.teams.out.logo
            ),
            player =
            PlayerDetails(
                id = transferResponse.player.id,
                name = transferResponse.player.name
            )
        )
    }

    private fun getCurrentSeason(): Int {
        return java.time.LocalDate.now().year
    }
}
