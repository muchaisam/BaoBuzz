package com.msdc.baobuzz.appnavigation

import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination
import com.msdc.baobuzz.R
import com.msdc.baobuzz.ui.theme.ThemeMode
import com.msdc.baobuzz.ui.theme.ThemeState

@Composable
fun BottomNavigation(
    currentDestination: NavDestination?,
    onNavigate: (Screen) -> Unit,
    themeState: ThemeState
) {
    val themeMode by themeState.themeMode
    val iconColor = if (themeMode == ThemeMode.Light) Color.Gray else Color.White

    NavigationBar(
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        NavigationBarItem(
            icon = { Image(painterResource(id = R.drawable.ic_home), contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentDestination?.route == Screen.Home.route,
            onClick = { onNavigate(Screen.Home) }
        )
        NavigationBarItem(
            icon = { Image(painterResource(id = R.drawable.competition), contentDescription = "Competition") },
            label = { Text("Browse") },
            selected = currentDestination?.route == Screen.Competition.route,
            onClick = { onNavigate(Screen.Competition) }
        )
        NavigationBarItem(
            icon = { Image(painterResource(id = R.drawable.ic_stats), contentDescription = "statistics") },
            label = { Text("Statistics") },
            selected = currentDestination?.route == Screen.Statistics.route,
            onClick = { onNavigate(Screen.Statistics) }
        )
        NavigationBarItem(
            icon = { Image(painterResource(id = R.drawable.ic_settings), contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = currentDestination?.route == Screen.Settings.route,
            onClick = { onNavigate(Screen.Settings) }
        )
    }
}