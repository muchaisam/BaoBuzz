package com.msdc.baobuzz.features.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.msdc.baobuzz.core.navigation.BaoBuzzRoutes
import com.msdc.baobuzz.features.home.HomeScreen
import com.msdc.baobuzz.features.leagues.LeaguesScreen
import com.msdc.baobuzz.features.settings.SettingsScreen
import com.msdc.baobuzz.features.stats.StatsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(navController: NavHostController = rememberNavController()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems =
        listOf(
            BottomNavItem(
                route = BaoBuzzRoutes.HOME,
                icon = Icons.Default.Home,
                label = "Home"
            ),
            BottomNavItem(
                route = BaoBuzzRoutes.LEAGUES,
                icon = Icons.Filled.List,
                label = "Leagues"
            ),
            BottomNavItem(
                route = BaoBuzzRoutes.STATS,
                icon = Icons.Default.Analytics,
                label = "Stats"
            ),
            BottomNavItem(
                route = BaoBuzzRoutes.SETTINGS,
                icon = Icons.Default.Settings,
                label = "Settings"
            )
        )

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(imageVector = item.icon, contentDescription = item.label)
                        },
                        label = { Text(item.label) },
                        selected =
                        currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(BaoBuzzRoutes.HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BaoBuzzRoutes.HOME,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BaoBuzzRoutes.HOME) {
                HomeScreen(
                    onNavigateToOnboarding = {
                        navController.navigate(BaoBuzzRoutes.ONBOARDING_WELCOME) {
                            popUpTo(BaoBuzzRoutes.HOME) { inclusive = true }
                        }
                    },
                    onNavigateToSettings = { navController.navigate(BaoBuzzRoutes.SETTINGS) },
                    // NEW FEATURE NAVIGATION 🎯📖🔄
                    onNavigateToQuiz = { navController.navigate(BaoBuzzRoutes.QUIZ) },
                    onNavigateToFacts = { navController.navigate(BaoBuzzRoutes.FACTS) },
                    onNavigateToComparison = { navController.navigate(BaoBuzzRoutes.COMPARISON) }
                )
            }

            composable(BaoBuzzRoutes.LEAGUES) { LeaguesScreen(navController = navController) }

            composable(BaoBuzzRoutes.STATS) { StatsScreen() }

            composable(BaoBuzzRoutes.SETTINGS) {
                SettingsScreen(
                    onNavigateToOnboarding = {
                        navController.navigate(BaoBuzzRoutes.ONBOARDING_WELCOME) {
                            popUpTo(BaoBuzzRoutes.HOME) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🚧 Under Construction", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "This screen will be implemented with modern Compose UI",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

data class BottomNavItem(val route: String, val icon: ImageVector, val label: String)
