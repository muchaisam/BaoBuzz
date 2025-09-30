package com.msdc.baobuzz.core.api

import com.msdc.baobuzz.core.cache.FootballDataCache
import com.msdc.baobuzz.core.models.*
import com.msdc.baobuzz.interfaces.FootballApi
import com.msdc.baobuzz.models.ApiTransfer
import com.msdc.baobuzz.models.PlayerStatResponse
import com.msdc.baobuzz.models.TransferDetail
import javax.inject.Inject
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
    private val footballApi: FootballApi,
    private val apiRequestTracker: ApiRequestTracker,
    private val cache: FootballDataCache
) : FootballRepository {

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
                                logo = fixtureResponse.teams.home.logo
                            ),
                            awayTeam =
                            Team(
                                id = fixtureResponse.teams.away.id,
                                name = fixtureResponse.teams.away.name,
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

            val response = footballApi.getStandings(league = leagueId, season = getCurrentSeason())

            val standings = response.response?.firstOrNull()?.league?.standings?.firstOrNull()
            val leagueInfo = response.response?.firstOrNull()?.league

            if (standings != null && leagueInfo != null) {
                apiRequestTracker.recordRequest()

                val leagueStanding =
                    LeagueStanding(
                        leagueId = leagueId,
                        leagueName = leagueInfo.name,
                        leagueLogo = leagueInfo.logo,
                        topTeams =
                        standings.map { standing ->
                            TeamStanding(
                                position = standing.rank,
                                team =
                                Team(
                                    id = standing.team.id,
                                    name = standing.team.name,
                                    logo = standing.team.logo
                                ),
                                points = standing.points,
                                played = standing.all.played,
                                won = standing.all.win,
                                drawn = standing.all.draw,
                                lost = standing.all.lose
                            )
                        }
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

            val response =
                footballApi.getFixtures(
                    league = leagueId,
                    season = getCurrentSeason(),
                    from = from,
                    to = to
                )

            val fixtures =
                response.response?.map { fixtureResponse: ApiFixture ->
                    com.msdc.baobuzz.core.models.Fixture(
                        id = fixtureResponse.fixture.id.toString(),
                        homeTeam =
                        Team(
                            id = fixtureResponse.teams.home.id,
                            name = fixtureResponse.teams.home.name,
                            logo = fixtureResponse.teams.home.logo
                        ),
                        awayTeam =
                        Team(
                            id = fixtureResponse.teams.away.id,
                            name = fixtureResponse.teams.away.name,
                            logo = fixtureResponse.teams.away.logo
                        ),
                        date = fixtureResponse.fixture.date,
                        venue = fixtureResponse.fixture.venue?.name ?: "TBD",
                        status = fixtureResponse.fixture.status.long,
                        homeScore = fixtureResponse.goals.home,
                        awayScore = fixtureResponse.goals.away
                    )
                }
                    ?: emptyList()

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

            val response = footballApi.getTopScorers(league = leagueId, season = getCurrentSeason())

            val players =
                response.response?.map { playerResponse: PlayerStatResponse ->
                    PlayerStat(
                        player =
                        Player(
                            id = playerResponse.player.id,
                            name = playerResponse.player.name
                        ),
                        photo = playerResponse.player.photo,
                        team =
                        Team(
                            id =
                            playerResponse.statistics.firstOrNull()
                                ?.team
                                ?.id
                                ?: 0,
                            name =
                            playerResponse.statistics.firstOrNull()
                                ?.team
                                ?.name
                                ?: "",
                            logo =
                            playerResponse.statistics.firstOrNull()
                                ?.team
                                ?.logo
                                ?: ""
                        ),
                        goals = playerResponse.statistics.firstOrNull()?.goals?.total ?: 0,
                        assists = playerResponse.statistics.firstOrNull()?.goals?.assists
                            ?: 0,
                        appearances =
                        playerResponse.statistics.firstOrNull()?.games?.appearences
                            ?: 0
                    )
                }
                    ?: emptyList()

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
                val response =
                    footballApi.getFixtures(
                        league = leagueId,
                        season = getCurrentSeason(),
                        from = today,
                        to = futureDate
                    )

                response.response.take(limit / leagueIds.size + 1).forEach { fixtureResponse ->
                    if (fixtureResponse.fixture.status.short == "NS") { // Not Started
                        fixtures.add(
                            UpcomingFixture(
                                id = fixtureResponse.fixture.id.toString(),
                                homeTeam =
                                Team(
                                    id = fixtureResponse.teams.home.id,
                                    name = fixtureResponse.teams.home.name,
                                    logo = fixtureResponse.teams.home.logo
                                ),
                                awayTeam =
                                Team(
                                    id = fixtureResponse.teams.away.id,
                                    name = fixtureResponse.teams.away.name,
                                    logo = fixtureResponse.teams.away.logo
                                ),
                                dateTime = fixtureResponse.fixture.date,
                                venue = fixtureResponse.fixture.venue.name,
                                round = fixtureResponse.league.round,
                                leagueId = leagueId,
                                leagueName = fixtureResponse.league.name
                            )
                        )
                    }
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
                val response =
                    footballApi.getFixtures(
                        league = leagueId,
                        season = getCurrentSeason(),
                        from = pastDate,
                        to = today
                    )

                response.response.take(limit / leagueIds.size + 1).forEach { fixtureResponse ->
                    if (fixtureResponse.fixture.status.short == "FT") { // Full Time
                        results.add(
                            RecentResult(
                                id = fixtureResponse.fixture.id.toString(),
                                homeTeam =
                                Team(
                                    id = fixtureResponse.teams.home.id,
                                    name = fixtureResponse.teams.home.name,
                                    logo = fixtureResponse.teams.home.logo
                                ),
                                awayTeam =
                                Team(
                                    id = fixtureResponse.teams.away.id,
                                    name = fixtureResponse.teams.away.name,
                                    logo = fixtureResponse.teams.away.logo
                                ),
                                homeScore = fixtureResponse.goals.home ?: 0,
                                awayScore = fixtureResponse.goals.away ?: 0,
                                date = fixtureResponse.fixture.date,
                                round = fixtureResponse.league.round,
                                leagueId = leagueId,
                                leagueName = fixtureResponse.league.name
                            )
                        )
                    }
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

            val targetSeason = season ?: getCurrentSeason()
            val standingsResponse =
                footballApi.getStandings(league = leagueId, season = targetSeason)

            val leagueInfo = standingsResponse.response.firstOrNull()?.league
            val standings =
                standingsResponse.response.firstOrNull()?.league?.standings?.firstOrNull()
            val champion = standings?.firstOrNull()

            if (leagueInfo != null) {
                // Get top scorer for the season
                val topScorersList = getTopScorers(leagueId)
                val topScorer = topScorersList.firstOrNull()

                apiRequestTracker.recordRequest()

                SeasonSummary(
                    leagueId = leagueId,
                    leagueName = leagueInfo.name,
                    leagueLogo = leagueInfo.logo,
                    season = targetSeason,
                    champion = champion?.let { Team(it.team.id, it.team.name, it.team.logo) },
                    topScorer = topScorer,
                    totalGoals = standings?.sumOf { it.all.goals.goalsfor } ?: 0,
                    totalMatches = standings?.sumOf { it.all.played } ?: 0,
                    isCurrentSeason = targetSeason == getCurrentSeason()
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
                            currentStanding = standings.topTeams.firstOrNull(),
                            nextFixture = upcomingFixtures.firstOrNull(),
                            lastResult = recentResults.firstOrNull(),
                            topScorer = topScorers.firstOrNull(),
                            matchesPlayed = standings.topTeams.firstOrNull()?.played ?: 0,
                            matchesRemaining =
                            38 -
                                    (standings.topTeams.firstOrNull()?.played
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
