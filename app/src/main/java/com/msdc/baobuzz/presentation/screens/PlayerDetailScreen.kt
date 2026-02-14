package com.msdc.baobuzz.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.msdc.baobuzz.presentation.components.charts.BarChart
import com.msdc.baobuzz.presentation.components.charts.ChartData
import com.msdc.baobuzz.presentation.components.charts.DonutChart
import com.msdc.baobuzz.presentation.components.charts.LineChart
import com.msdc.baobuzz.presentation.components.charts.LineChartPoint

// Local data class for player details display
data class PlayerDetails(
    val id: Int,
    val name: String,
    val position: String,
    val team: String,
    val age: Int,
    val nationality: String,
    val goals: Int,
    val assists: Int,
    val appearances: Int,
    val rating: Double,
    val photo: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDetailScreen(playerId: Int, onBackPressed: () -> Unit, modifier: Modifier = Modifier) {
    // Sample player data - in real app, this would come from ViewModel
    val player = remember {
        PlayerDetails(
            id = playerId,
            name = "Marcus Rashford",
            position = "Forward",
            team = "Manchester United",
            age = 26,
            nationality = "England",
            goals = 15,
            assists = 8,
            appearances = 28,
            rating = 8.2
        )
    }

    var isFavorite by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Stats", "Performance", "History")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = player.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        Icon(
                            imageVector =
                                if (isFavorite) Icons.Filled.Star
                                else Icons.Outlined.StarBorder,
                            contentDescription =
                                if (isFavorite) "Remove from favorites"
                                else "Add to favorites",
                            tint =
                                if (isFavorite) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Player Header Card
            item { PlayerHeaderCard(player = player) }

            // Tab Row
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight =
                                        if (selectedTab == index) FontWeight.Bold
                                        else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }

            // Tab Content
            item {
                when (selectedTab) {
                    0 -> PlayerOverviewContent(player)
                    1 -> PlayerStatsContent(player)
                    2 -> PlayerPerformanceContent(player)
                    3 -> PlayerHistoryContent(player)
                }
            }
        }
    }
}

@Composable
private fun PlayerHeaderCard(player: PlayerDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Player Avatar Placeholder
            Box(
                modifier =
                    Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.name.split(" ").map { it.first() }.joinToString(""),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "${player.position} • ${player.team}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Text(
                    text = "${player.age} years • ${player.nationality}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                )
            }

            // Rating Badge
            Card(
                shape = CircleShape,
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
            ) {
                Text(
                    text = player.rating.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun PlayerOverviewContent(player: PlayerDetails) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Quick Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Goals",
                value = player.goals.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Assists",
                value = player.assists.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Apps",
                value = player.appearances.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        // Performance Overview Chart
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Performance Overview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                DonutChart(
                    data =
                        listOf(
                            ChartData(
                                "Goals",
                                player.goals.toFloat(),
                                MaterialTheme.colorScheme.primary
                            ),
                            ChartData(
                                "Assists",
                                player.assists.toFloat(),
                                MaterialTheme.colorScheme.secondary
                            ),
                            ChartData(
                                "Clean Sheets",
                                8f,
                                MaterialTheme.colorScheme.tertiary
                            )
                        ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }
    }
}

@Composable
private fun PlayerStatsContent(player: PlayerDetails) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Detailed Stats Cards
        val statsList =
            listOf(
                "Goals per Match" to
                        (player.goals.toFloat() / player.appearances).toString().take(4),
                "Assists per Match" to
                        (player.assists.toFloat() / player.appearances).toString().take(4),
                "Pass Accuracy" to "87%",
                "Shots per Match" to "3.2",
                "Key Passes" to "2.1",
                "Dribbles Success" to "78%"
            )

        statsList.chunked(2).forEach { rowStats ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowStats.forEach { (title, value) ->
                    StatCard(title = title, value = value, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PlayerPerformanceContent(player: PlayerDetails) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Performance Chart
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Goals & Assists Trend",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                LineChart(
                    points =
                        listOf(
                            LineChartPoint(1f, 2f),
                            LineChartPoint(2f, 4f),
                            LineChartPoint(3f, 3f),
                            LineChartPoint(4f, 6f),
                            LineChartPoint(5f, 8f),
                            LineChartPoint(6f, 7f)
                        ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }

        // Form Chart
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Recent Form",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                BarChart(
                    data =
                        listOf(
                            ChartData("W", 3f, Color.Green),
                            ChartData("W", 2f, Color.Green),
                            ChartData("D", 1f, Color.Yellow),
                            ChartData("L", 0f, Color.Red),
                            ChartData("W", 2f, Color.Green)
                        ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
            }
        }
    }
}

@Composable
private fun PlayerHistoryContent(player: PlayerDetails) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Career History
        val careerHistory =
            listOf(
                "2023-24" to "Manchester United • 28 apps, 15 goals",
                "2022-23" to "Manchester United • 35 apps, 17 goals",
                "2021-22" to "Manchester United • 32 apps, 22 goals",
                "2020-21" to "Manchester United • 37 apps, 11 goals"
            )

        careerHistory.forEach { (season, stats) ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = season,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stats,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
