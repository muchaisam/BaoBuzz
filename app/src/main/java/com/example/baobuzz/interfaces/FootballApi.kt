package com.example.baobuzz.interfaces

import com.example.baobuzz.models.FixturesResponse
import com.example.baobuzz.models.TransfersResponse
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
    suspend fun getTransfers(
        @Query("team") teamId: Int,
        @Query("season") season: Int
    ): TransfersResponse

}