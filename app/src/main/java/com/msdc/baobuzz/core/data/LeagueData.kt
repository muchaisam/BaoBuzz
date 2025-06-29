package com.msdc.baobuzz.core.data

import androidx.compose.ui.graphics.Color
import com.msdc.baobuzz.models.League

/**
 * Static data for popular football leagues with real API IDs and logo URLs
 */
object LeagueData {
    
    fun getPopularLeagues(): List<League> = listOf(
        League(
            id = 39,
            name = "Premier League",
            type = "League",
            country = "England",
            logo = "https://media.api-sports.io/football/leagues/39.png",
            flag = "https://media.api-sports.io/flags/gb.svg",
            season = 2024,
            round = null
        ),
        League(
            id = 140,
            name = "La Liga",
            type = "League", 
            country = "Spain",
            logo = "https://media.api-sports.io/football/leagues/140.png",
            flag = "https://media.api-sports.io/flags/es.svg",
            season = 2024,
            round = null
        ),
        League(
            id = 78,
            name = "Bundesliga",
            type = "League",
            country = "Germany", 
            logo = "https://media.api-sports.io/football/leagues/78.png",
            flag = "https://media.api-sports.io/flags/de.svg",
            season = 2024,
            round = null
        ),
        League(
            id = 135,
            name = "Serie A",
            type = "League",
            country = "Italy",
            logo = "https://media.api-sports.io/football/leagues/135.png", 
            flag = "https://media.api-sports.io/flags/it.svg",
            season = 2024,
            round = null
        ),
        League(
            id = 61,
            name = "Ligue 1",
            type = "League",
            country = "France",
            logo = "https://media.api-sports.io/football/leagues/61.png",
            flag = "https://media.api-sports.io/flags/fr.svg",
            season = 2024,
            round = null
        )
    )
    
    /**
     * Extended league information for onboarding UI
     */
    data class OnboardingLeague(
        val league: League,
        val primaryColor: Color,
        val description: String,
        val isPopular: Boolean = true
    )
    
    fun getOnboardingLeagues(): List<OnboardingLeague> = listOf(
        OnboardingLeague(
            league = getPopularLeagues()[0], // Premier League
            primaryColor = Color(0xFF3D195B),
            description = "The most competitive league in the world"
        ),
        OnboardingLeague(
            league = getPopularLeagues()[1], // La Liga
            primaryColor = Color(0xFFFF6B00),
            description = "Home to the world's greatest talents"
        ),
        OnboardingLeague(
            league = getPopularLeagues()[2], // Bundesliga
            primaryColor = Color(0xFFD20515),
            description = "Known for passionate fans and attacking football"
        ),
        OnboardingLeague(
            league = getPopularLeagues()[3], // Serie A
            primaryColor = Color(0xFF004E9F),
            description = "Tactical excellence and rich history"
        ),
        OnboardingLeague(
            league = getPopularLeagues()[4], // Ligue 1
            primaryColor = Color(0xFF1E3A8A),
            description = "Emerging talents and exciting gameplay"
        )
    )
    
    /**
     * Get league by ID
     */
    fun getLeagueById(id: Int): League? = getPopularLeagues().find { it.id == id }
    
    /**
     * Get onboarding league by ID
     */
    fun getOnboardingLeagueById(id: Int): OnboardingLeague? = 
        getOnboardingLeagues().find { it.league.id == id }
}
