package com.example.baobuzz.interfaces

import com.example.baobuzz.models.Fixture
import com.example.baobuzz.models.FixturesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FootballApi {
    @GET("fixtures")
    suspend fun getUpcomingFixtures(
        @Query("league") leagueId: Int,
        @Query("next") next: Int,
        @Query("timezone") timezone: String
    ): FixturesResponse

    data class FixturesResponse(
        val response: List<Fixture>
    )
}