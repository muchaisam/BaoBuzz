package com.msdc.baobuzz.appnavigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.msdc.baobuzz.components.home.HomeScreen
import com.msdc.baobuzz.ui.screens.CompetitionScreen
import com.msdc.baobuzz.ui.screens.SettingsScreen
import com.msdc.baobuzz.ui.screens.StatisticsScreen
import com.msdc.baobuzz.ui.theme.ThemeMode
import com.msdc.baobuzz.ui.theme.ThemeState
import com.msdc.baobuzz.ui.theme.rememberThemeState
import com.msdc.baobuzz.viewmodel.CoachViewModel
import com.msdc.baobuzz.viewmodel.HomeViewModel
import com.msdc.baobuzz.viewmodel.StandingsViewModel

@Composable
fun BaoBuzz (
    viewModel: CoachViewModel = hiltViewModel(),
    standingsViewModel: StandingsViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
    themeState: ThemeState = rememberThemeState()
) {
    val themeMode by themeState.themeMode

    val colors = when (themeMode) {
        ThemeMode.Light -> lightColorScheme()
        ThemeMode.Dark -> darkColorScheme()
    }


    MaterialTheme(
    colorScheme = colors,
    typography = Typography()
    ) {
        Scaffold(
            bottomBar = {
                BottomNavigation(
                    currentDestination = navController.currentDestination,
                    onNavigate = { screen ->
                        navController.navigate(screen.route)
                    },
                    themeState = themeState
                )
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        coachViewModel = viewModel,
                        standingsViewModel = standingsViewModel,
                        navController = navController as NavHostController
                    )
                }
                composable(Screen.Competition.route) {
                    CompetitionScreen()
                }
                composable(Screen.Statistics.route) {
                    StatisticsScreen()
                }
                composable(Screen.Settings.route) {
                    SettingsScreen()
                }
            }
        }
    }
}
