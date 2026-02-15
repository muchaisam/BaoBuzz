package com.msdc.baobuzz.interfaces

import com.msdc.baobuzz.models.ApiResponse
import com.msdc.baobuzz.models.Coach
import com.msdc.baobuzz.models.FixturesResponse
import com.msdc.baobuzz.models.LeagueWrapper
import com.msdc.baobuzz.models.LineupsResponse
import com.msdc.baobuzz.models.PredictionsResponse
import com.msdc.baobuzz.models.StandingsResponse
import com.msdc.baobuzz.models.StatisticsResponse
import com.msdc.baobuzz.models.TeamWrapper
import com.msdc.baobuzz.models.TopAssistersResponse
import com.msdc.baobuzz.models.TopScorersResponse
import com.msdc.baobuzz.models.TransfersResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FootballApi {
    @GET("fixtures")
    suspend fun getUpcomingFixtures(
        @Query("league") leagueId: Int,
        @Query("next") next: Int,
        @Query("timezone") timezone: String,
    ): FixturesResponse

    @GET("fixtures")
    suspend fun getFixtures(
        @Query("league") league: Int? = null,
        @Query("season") season: Int? = null,
        @Query("live") live: String? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null
    ): FixturesResponse

    @GET("transfers")
    suspend fun getTransfers(@Query("team") team: Int? = null): TransfersResponse

    @GET("transfers")
    suspend fun getTransfersByTeam(@Query("team") team: Int): TransfersResponse

    @GET("standings")
    suspend fun getStandings(
        @Query("league") league: Int,
        @Query("season") season: Int
    ): StandingsResponse

    @GET("players/topscorers")
    suspend fun getTopScorers(
        @Query("league") league: Int,
        @Query("season") season: Int
    ): TopScorersResponse

    @GET("players/topassists")
    suspend fun getTopAssisters(
        @Query("league") league: Int,
        @Query("season") season: Int
    ): TopAssistersResponse

    @GET("coachs")
    suspend fun getCoach(@Query("id") id: Int): ApiResponse<Coach>

    @GET("leagues")
    suspend fun getLeague(
        @Query("id") id: Int,
        @Query("current") current: Boolean = true
    ): ApiResponse<LeagueWrapper>

    @GET("teams")
    suspend fun getTeams(
        @Query("league") league: Int,
        @Query("season") season: Int
    ): ApiResponse<TeamWrapper>

    @GET("fixtures")
    suspend fun getFixtureDetails(@Query("id") fixtureId: Int): FixturesResponse

    @GET("fixtures/headtohead")
    suspend fun getHeadToHead(@Query("h2h") h2h: String): FixturesResponse

    @GET("fixtures/statistics")
    suspend fun getFixtureStatistics(@Query("fixture") fixtureId: Int): StatisticsResponse

    @GET("fixtures/lineups")
    suspend fun getFixtureLineups(@Query("fixture") fixtureId: Int): LineupsResponse

    @GET("predictions")
    suspend fun getFixturePredictions(@Query("fixture") fixtureId: Int): PredictionsResponse

    @GET("fixtures")
    suspend fun getFixturesByDate(
        @Query("date") date: String,
        @Query("league") leagues: String
    ): FixturesResponse
}
