package com.msdc.baobuzz.interfaces

import com.msdc.baobuzz.models.ApiResponse
import com.msdc.baobuzz.models.Coach
import com.msdc.baobuzz.models.FixturesResponse
import com.msdc.baobuzz.models.StandingsResponse
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

}