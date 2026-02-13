package com.msdc.baobuzz.core.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.msdc.baobuzz.features.main.MainAppScreen
import com.msdc.baobuzz.features.onboarding.OnboardingLeagueSelectionScreen
import com.msdc.baobuzz.features.onboarding.OnboardingWelcomeScreen
import com.msdc.baobuzz.features.splash.SplashScreen
import com.msdc.baobuzz.presentation.transfers.TransfersScreen

@Composable
fun BaoBuzzNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = BaoBuzzRoutes.SPLASH,
        enterTransition = {
            fadeIn(animationSpec = tween(300, easing = EaseInOut)) +
                    slideIntoContainer(
                        animationSpec = tween(300, easing = EaseInOut),
                        towards = AnimatedContentTransitionScope.SlideDirection.Start
                    )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300, easing = EaseInOut)) +
                    slideOutOfContainer(
                        animationSpec = tween(300, easing = EaseInOut),
                        towards = AnimatedContentTransitionScope.SlideDirection.Start
                    )
        }
    ) {
        // Splash Screen with fade transition
        composable(
            route = BaoBuzzRoutes.SPLASH,
            enterTransition = { fadeIn(animationSpec = tween(500, easing = LinearEasing)) },
            exitTransition = { fadeOut(animationSpec = tween(300, easing = LinearEasing)) }
        ) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(BaoBuzzRoutes.ONBOARDING_WELCOME) {
                        popUpTo(BaoBuzzRoutes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToMainApp = {
                    navController.navigate(BaoBuzzRoutes.MAIN_APP) {
                        popUpTo(BaoBuzzRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // Onboarding Welcome with slide animation
        composable(
            route = BaoBuzzRoutes.ONBOARDING_WELCOME,
            enterTransition = {
                slideIntoContainer(
                    animationSpec = tween(400, easing = EaseInOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(400, easing = EaseInOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ) {
            OnboardingWelcomeScreen(
                onContinue = { navController.navigate(BaoBuzzRoutes.ONBOARDING_LEAGUES) }
            )
        }

        // League Selection with slide animation
        composable(
            route = BaoBuzzRoutes.ONBOARDING_LEAGUES,
            enterTransition = {
                slideIntoContainer(
                    animationSpec = tween(400, easing = EaseInOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(400, easing = EaseInOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ) {
            OnboardingLeagueSelectionScreen(
                onContinue = { _ ->
                    // For now, skip team selection and go straight to main app
                    // In a full implementation, you'd navigate to team selection
                    navController.navigate(BaoBuzzRoutes.MAIN_APP) {
                        popUpTo(BaoBuzzRoutes.ONBOARDING_WELCOME) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Main App with elegant fade-in
        composable(
            route = BaoBuzzRoutes.MAIN_APP,
            enterTransition = {
                fadeIn(animationSpec = tween(500, easing = EaseInOut)) +
                        slideIntoContainer(
                            animationSpec = tween(500, easing = EaseInOut),
                            towards = AnimatedContentTransitionScope.SlideDirection.Up
                        )
            }
        ) { MainAppScreen() }

        // 🎯 QUIZ FEATURE - Daily Football Quiz
        composable(
            route = BaoBuzzRoutes.QUIZ,
            enterTransition = {
                fadeIn(animationSpec = tween(400, easing = EaseInOut)) +
                        slideIntoContainer(
                            animationSpec = tween(400, easing = EaseInOut),
                            towards = AnimatedContentTransitionScope.SlideDirection.Up
                        )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300, easing = EaseInOut)) +
                        slideOutOfContainer(
                            animationSpec = tween(300, easing = EaseInOut),
                            towards = AnimatedContentTransitionScope.SlideDirection.Down
                        )
            }
        ) {
            com.msdc.baobuzz.features.quiz.QuizScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 📖 FACTS FEATURE - Historical Football Facts
        composable(
            route = BaoBuzzRoutes.FACTS,
            enterTransition = {
                fadeIn(animationSpec = tween(400, easing = EaseInOut)) +
                        slideIntoContainer(
                            animationSpec = tween(400, easing = EaseInOut),
                            towards = AnimatedContentTransitionScope.SlideDirection.Start
                        )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300, easing = EaseInOut)) +
                        slideOutOfContainer(
                            animationSpec = tween(300, easing = EaseInOut),
                            towards = AnimatedContentTransitionScope.SlideDirection.End
                        )
            }
        ) {
            com.msdc.baobuzz.features.facts.FactsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 🔄 COMPARISON FEATURE - Season Comparison
        composable(
            route = BaoBuzzRoutes.COMPARISON,
            enterTransition = {
                fadeIn(animationSpec = tween(400, easing = EaseInOut)) +
                        slideIntoContainer(
                            animationSpec = tween(400, easing = EaseInOut),
                            towards = AnimatedContentTransitionScope.SlideDirection.Start
                        )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300, easing = EaseInOut)) +
                        slideOutOfContainer(
                            animationSpec = tween(300, easing = EaseInOut),
                            towards = AnimatedContentTransitionScope.SlideDirection.End
                        )
            }
        ) {
            com.msdc.baobuzz.features.comparison.ComparisonScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Transfers Screen
        composable(BaoBuzzRoutes.TRANSFERS) {
            val teamId = it.arguments?.getString("teamId")?.toIntOrNull()
            if (teamId != null) {
                TransfersScreen(teamId = teamId, navController = navController)
            }
        }

        // Team Detail Screen
        composable(BaoBuzzRoutes.TEAM_DETAIL) {
            val teamId = it.arguments?.getString("teamId")?.toIntOrNull()
            if (teamId != null) {
                com.msdc.baobuzz.presentation.screens.TeamDetailScreen(
                    teamId = teamId,
                    onBackPressed = { navController.popBackStack() }
                )
            }
        }

        // Analytics Screen
        composable(BaoBuzzRoutes.ANALYTICS) {
            com.msdc.baobuzz.presentation.screens.AnalyticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Player Detail Screen
        composable(BaoBuzzRoutes.PLAYER_DETAIL) {
            val playerId = it.arguments?.getString("playerId")?.toIntOrNull()
            if (playerId != null) {
                com.msdc.baobuzz.presentation.screens.PlayerDetailScreen(
                    playerId = playerId,
                    onBackPressed = { navController.popBackStack() }
                )
            }
        }

        // Match Detail Screen
        composable(BaoBuzzRoutes.MATCH_DETAIL) {
            val matchId = it.arguments?.getString("matchId")?.toIntOrNull()
            if (matchId != null) {
                com.msdc.baobuzz.presentation.screens.MatchDetailScreen(
                    matchId = matchId,
                    onBackPressed = { navController.popBackStack() }
                )
            }
        }

        // Search Screen
        composable(BaoBuzzRoutes.SEARCH) {
            com.msdc.baobuzz.presentation.screens.SearchScreen(
                onBackPressed = { navController.popBackStack() },
                onNavigateToTeam = { teamId -> navController.navigate("team_detail/$teamId") },
                onNavigateToPlayer = { playerId ->
                    navController.navigate("player_detail/$playerId")
                },
                onNavigateToMatch = { matchId ->
                    navController.navigate("match_detail/$matchId")
                }
            )
        }

        // Favorites Screen
        composable(BaoBuzzRoutes.FAVORITES) {
            com.msdc.baobuzz.presentation.screens.FavoritesScreen(
                onBackPressed = { navController.popBackStack() },
                onNavigateToTeam = { teamId -> navController.navigate("team_detail/$teamId") },
                onNavigateToPlayer = { playerId ->
                    navController.navigate("player_detail/$playerId")
                }
            )
        }
    }
}
