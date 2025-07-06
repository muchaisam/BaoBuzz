package com.msdc.baobuzz.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.msdc.baobuzz.features.main.MainAppScreen
import com.msdc.baobuzz.features.onboarding.OnboardingLeagueSelectionScreen
import com.msdc.baobuzz.features.onboarding.OnboardingWelcomeScreen
import com.msdc.baobuzz.features.splash.SplashScreen

@Composable
fun BaoBuzzNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = BaoBuzzRoutes.SPLASH
    ) {
        // Splash Screen
        composable(BaoBuzzRoutes.SPLASH) {
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

        // Onboarding Welcome
        composable(BaoBuzzRoutes.ONBOARDING_WELCOME) {
            OnboardingWelcomeScreen(
                onContinue = {
                    navController.navigate(BaoBuzzRoutes.ONBOARDING_LEAGUES)
                }
            )
        }

        // League Selection
        composable(BaoBuzzRoutes.ONBOARDING_LEAGUES) {
            OnboardingLeagueSelectionScreen(
                onContinue = { selectedLeagues ->
                    // For now, skip team selection and go straight to main app
                    // In a full implementation, you'd navigate to team selection
                    navController.navigate(BaoBuzzRoutes.MAIN_APP) {
                        popUpTo(BaoBuzzRoutes.ONBOARDING_WELCOME) { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Main App
        composable(BaoBuzzRoutes.MAIN_APP) {
            MainAppScreen()
        }
    }
}
