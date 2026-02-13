package com.msdc.baobuzz.core.api.interfaces

import com.msdc.baobuzz.models.footballdata.FDCompetitionsResponse
import com.msdc.baobuzz.models.footballdata.FDMatchResponse
import com.msdc.baobuzz.models.footballdata.FDMatchesResponse
import com.msdc.baobuzz.models.footballdata.FDScorersResponse
import com.msdc.baobuzz.models.footballdata.FDStandingsResponse
import com.msdc.baobuzz.models.footballdata.FDTeamResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Football-Data.org API Interface (v4)
 *
 * Free tier: 10 requests per minute
 * Requires API key in header: X-Auth-Token
 *
 * Base URL: https://api.football-data.org/v4/
 * Get API key: https://www.football-data.org/client/register
 *
 * Supported competitions:
 * - PL = Premier League
 * - PD = La Liga
 * - BL1 = Bundesliga
 * - SA = Serie A
 * - FL1 = Ligue 1
 * - CL = Champions League
 * - WC = World Cup
 *
 * Rate limit: 10 calls/min on free tier
 */
interface FootballDataApi {

    /**
     * Get all available competitions
     *
     * @return List of all competitions with IDs and details
     */
    @GET("competitions")
    suspend fun getCompetitions(): FDCompetitionsResponse

    /**
     * Get standings for a specific competition
     *
     * @param competitionCode Competition code (e.g., "PL", "PD", "CL")
     * @return Current season standings
     */
    @GET("competitions/{code}/standings")
    suspend fun getStandings(
        @Path("code") competitionCode: String
    ): FDStandingsResponse

    /**
     * Get matches for a specific competition
     *
     * @param competitionCode Competition code
     * @param status Match status: SCHEDULED, LIVE, IN_PLAY, PAUSED, FINISHED, POSTPONED, SUSPENDED, CANCELLED
     * @param dateFrom Start date (format: YYYY-MM-DD)
     * @param dateTo End date (format: YYYY-MM-DD)
     * @return List of matches matching criteria
     */
    @GET("competitions/{code}/matches")
    suspend fun getMatches(
        @Path("code") competitionCode: String,
        @Query("status") status: String? = null,
        @Query("dateFrom") dateFrom: String? = null,
        @Query("dateTo") dateTo: String? = null
    ): FDMatchesResponse

    /**
     * Get top scorers for a specific competition
     *
     * @param competitionCode Competition code
     * @param limit Number of scorers to return (default: 10)
     * @return List of top scorers with statistics
     */
    @GET("competitions/{code}/scorers")
    suspend fun getTopScorers(
        @Path("code") competitionCode: String,
        @Query("limit") limit: Int = 10
    ): FDScorersResponse

    /**
     * Get details for a specific team
     *
     * @param teamId Team ID from football-data.org
     * @return Team details including squad, venue, etc.
     */
    @GET("teams/{id}")
    suspend fun getTeam(
        @Path("id") teamId: Int
    ): FDTeamResponse

    /**
     * Get matches for a specific team
     *
     * @param teamId Team ID
     * @param status Match status filter
     * @param dateFrom Start date
     * @param dateTo End date
     * @param limit Number of matches to return
     * @return List of team matches
     */
    @GET("teams/{id}/matches")
    suspend fun getTeamMatches(
        @Path("id") teamId: Int,
        @Query("status") status: String? = null,
        @Query("dateFrom") dateFrom: String? = null,
        @Query("dateTo") dateTo: String? = null,
        @Query("limit") limit: Int = 50
    ): FDMatchesResponse

    /**
     * Get details for a specific match
     *
     * @param matchId Match ID
     * @return Complete match details including lineups, events, etc.
     */
    @GET("matches/{id}")
    suspend fun getMatch(
        @Path("id") matchId: Int
    ): FDMatchResponse
}
