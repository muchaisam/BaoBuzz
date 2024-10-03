package com.msdc.baobuzz.interfaces

import com.msdc.baobuzz.models.ApiResponse
import com.msdc.baobuzz.models.Coach
import com.msdc.baobuzz.models.FixturesResponse
import com.msdc.baobuzz.models.League
import com.msdc.baobuzz.models.LeagueResponse
import com.msdc.baobuzz.models.StandingsResponse
import com.msdc.baobuzz.models.Team
import com.msdc.baobuzz.models.TeamResponse
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


    @GET("transfers")
    suspend fun getTransfers(@Query("team") teamId: Int): TransfersResponse

    @GET("standings")
    suspend fun getStandings(
        @Query("league") league: Int,
        @Query("season") season: Int
    ): StandingsResponse

    @GET("coachs")
    suspend fun getCoach(@Query("id") id: Int): ApiResponse<Coach>

    // New methods
    @GET("leagues")
    suspend fun getLeagues(@Query("current") current: Boolean): LeagueResponse

    @GET("leagues")
    suspend fun searchLeagues(@Query("search") query: String): LeagueResponse

    @GET("teams")
    suspend fun getTeams(
        @Query("league") league: Int,
        @Query("season") season: Int
    ): TeamResponse

    @GET("teams")
    suspend fun searchTeams(@Query("search") query: String): TeamResponse
}
