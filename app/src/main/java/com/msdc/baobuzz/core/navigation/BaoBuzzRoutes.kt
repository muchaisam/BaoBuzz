package com.msdc.baobuzz.core.navigation

/** Navigation routes for the app */
object BaoBuzzRoutes {
    const val SPLASH = "splash"
    const val ONBOARDING_WELCOME = "onboarding_welcome"
    const val ONBOARDING_FEATURES = "onboarding_features"
    const val ONBOARDING_LEAGUES = "onboarding_leagues"
    const val ONBOARDING_TEAMS = "onboarding_teams"
    const val ONBOARDING = "onboarding"
    const val MAIN_APP = "main_app"

    // Main app destinations
    const val HOME = "home"
    const val LEAGUES = "leagues"
    const val STATS = "stats"
    const val SETTINGS = "settings"
    const val TRANSFERS = "transfers/{teamId}"

    // NEW FEATURE DESTINATIONS 🎯🔥
    const val QUIZ = "quiz"
    const val QUIZ_RESULT = "quiz_result/{quizId}"
    const val FACTS = "facts"
    const val COMPARISON = "comparison"

    // Premium feature destinations
    const val TEAM_DETAIL = "team_detail/{teamId}"
    const val PLAYER_DETAIL = "player_detail/{playerId}"
    const val MATCH_DETAIL = "match_detail/{matchId}"
    const val ANALYTICS = "analytics"
    const val SEARCH = "search"
    const val FAVORITES = "favorites"
}
