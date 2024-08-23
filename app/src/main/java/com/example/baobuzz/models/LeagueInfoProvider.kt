package com.example.baobuzz.models

class LeagueInfoProvider {
    private val leagueInfo = mapOf(
        39 to LeagueInfo(39, "Premier League", "England", "https://media-3.api-sports.io/football/leagues/39.png"),
        140 to LeagueInfo(140, "La Liga", "Spain", "https://media-3.api-sports.io/football/leagues/140.png"),
        61 to LeagueInfo(61, "Ligue 1", "France", "https://media-3.api-sports.io/football/leagues/61.png"),
        78 to LeagueInfo(78, "Bundesliga", "Germany", "https://media-3.api-sports.io/football/leagues/78.png"),
        135 to LeagueInfo(135, "Serie A", "Italy", "https://media-3.api-sports.io/football/leagues/135.png")
    )

    fun getLeagueInfo(leagueId: Int): LeagueInfo = leagueInfo[leagueId] ?: LeagueInfo(0, "Unknown League", "Unknown Country", "")
    fun getAllLeagueInfo(): List<LeagueInfo> = leagueInfo.values.toList()
}
