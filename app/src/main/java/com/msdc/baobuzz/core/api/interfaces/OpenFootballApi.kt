package com.msdc.baobuzz.core.api.interfaces

import com.msdc.baobuzz.models.openfootball.OFSeasonResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * OpenFootball API Interface
 *
 * Free historical football data from 1950s to present.
 * Data source: GitHub repository (openfootball/football.json)
 * No authentication required.
 *
 * Base URL: https://raw.githubusercontent.com/openfootball/football.json/master/
 *
 * Supported leagues:
 * - en.1 = English Premier League
 * - de.1 = German Bundesliga
 * - es.1 = Spanish La Liga
 * - it.1 = Italian Serie A
 * - fr.1 = French Ligue 1
 * - br.1 = Brazilian Serie A
 *
 * Example: https://raw.githubusercontent.com/openfootball/football.json/master/2015-16/en.1.json
 */
interface OpenFootballApi {

    /**
     * Get all matches for a specific league and season
     *
     * @param season Format: "2015-16", "2020-21", etc.
     * @param league Format: "en.1" (Premier League), "es.1" (La Liga), etc.
     * @return Complete season data with all matches
     */
    @GET("{season}/{league}.json")
    suspend fun getSeasonMatches(
        @Path("season") season: String,
        @Path("league") league: String
    ): OFSeasonResponse

    /**
     * Get historical standings for a league season
     * Note: Not all seasons have standings data
     *
     * @param season Format: "2015-16"
     * @param league Format: "en.1"
     * @return Season standings if available
     */
    @GET("{season}/{league}.standings.json")
    suspend fun getSeasonStandings(
        @Path("season") season: String,
        @Path("league") league: String
    ): OFSeasonResponse

    /**
     * Get top scorers for a league season
     * Note: Not all seasons have scorer data
     *
     * @param season Format: "2015-16"
     * @param league Format: "en.1"
     * @return Season top scorers if available
     */
    @GET("{season}/{league}.scorers.json")
    suspend fun getSeasonScorers(
        @Path("season") season: String,
        @Path("league") league: String
    ): OFSeasonResponse
}
