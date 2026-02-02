package com.msdc.baobuzz.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.msdc.baobuzz.models.Match
import com.msdc.baobuzz.models.Team
import com.msdc.baobuzz.presentation.components.charts.*

data class MatchPlayer(
        val id: Int,
        val name: String,
        val position: String,
        val rating: Double,
        val goals: Int = 0,
        val assists: Int = 0,
        val yellowCards: Int = 0,
        val redCards: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(matchId: Int, onBackPressed: () -> Unit, modifier: Modifier = Modifier) {
    // Sample match data - in real app, this would come from ViewModel
    val match = remember {
        com.msdc.baobuzz.models.Match(
                id = matchId,
                homeTeam = Team(1, "Manchester United", "MUN", "England", 1878, false, "https://media-4.api-sports.io/football/teams/33.png"),
                awayTeam = Team(2, "Liverpool", "LIV", "England", 1892, false, "https://media-4.api-sports.io/football/teams/40.png"),
                date = "2024-01-15T15:30:00",
                status = "FT",
                league = "Premier League",
                score = com.msdc.baobuzz.models.Score(
                    halftime = com.msdc.baobuzz.models.Goals(1, 0),
                    fulltime = com.msdc.baobuzz.models.Goals(2, 1),
                    extratime = null,
                    penalty = null
                )
        )
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Lineups", "Stats", "Timeline")

    Scaffold(
            topBar = {
                TopAppBar(
                        title = {
                            Text(
                                    text = "${match.homeTeam.name} vs ${match.awayTeam.name}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onBackPressed) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                modifier = modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Match Header Card
            item { MatchHeaderCard(match = match) }

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
                    0 -> MatchOverviewContent(match)
                    1 -> MatchLineupsContent(match)
                    2 -> MatchStatsContent(match)
                    3 -> MatchTimelineContent(match)
                }
            }
        }
    }
}

@Composable
private fun MatchHeaderCard(match: Match) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
    ) {
        Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Teams and Score
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Home Team
                TeamDisplay(
                        teamName = match.homeTeam.name,
                        teamCode = match.homeTeam.shortName ?: match.homeTeam.code ?: "N/A",
                        modifier = Modifier.weight(1f),
                        alignment = Alignment.Start
                )

                // Score
                Card(
                        shape = RoundedCornerShape(12.dp),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                )
                ) {
                    Text(
                            text = "${match.score.fulltime?.home ?: 0} - ${match.score.fulltime?.away ?: 0}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }

                // Away Team
                TeamDisplay(
                        teamName = match.awayTeam.name,
                        teamCode = match.awayTeam.shortName ?: match.awayTeam.code ?: "N/A",
                        modifier = Modifier.weight(1f),
                        alignment = Alignment.End
                )
            }

            // Match Info Row
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MatchInfoItem(
                        icon = Icons.Default.AccessTime,
                        text = match.status,
                        modifier = Modifier.weight(1f)
                )
                MatchInfoItem(
                        icon = Icons.Default.Stadium,
                        text = "Stadium", // venue not in model
                        modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TeamDisplay(
        teamName: String,
        teamCode: String,
        alignment: Alignment.Horizontal,
        modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = alignment) {
        // Team Logo Placeholder
        Box(
                modifier =
                        Modifier.size(60.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
        ) {
            Text(
                    text = teamCode,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
                text = teamName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign =
                        when (alignment) {
                            Alignment.CenterStart -> TextAlign.Start
                            Alignment.CenterEnd -> TextAlign.End
                            else -> TextAlign.Center
                        }
        )
    }
}

@Composable
private fun MatchInfoItem(icon: ImageVector, text: String, modifier: Modifier = Modifier) {
    Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
    ) {
        Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun MatchOverviewContent(match: Match) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Key Match Stats
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                        text = "Match Statistics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )

                // Possession Chart
                DonutChart(
                        data =
                                listOf(
                                        ChartData(
                                                match.homeTeam.shortName ?: match.homeTeam.code ?: "Home",
                                                60f,
                                                MaterialTheme.colorScheme.primary
                                        ),
                                        ChartData(
                                                match.awayTeam.shortName ?: match.awayTeam.code ?: "Away",
                                                40f,
                                                MaterialTheme.colorScheme.secondary
                                        )
                                ),
                        modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            }
        }

        // Goals
        GoalsList(match = match)
    }
}

@Composable
private fun GoalsList(match: Match) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                    text = "Goals",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
            )

            // Sample goals - in real app, this would be actual goal data
            listOf(
                            "15' Marcus Rashford (${match.homeTeam.shortName})",
                            "42' Mohamed Salah (${match.awayTeam.shortName})",
                            "78' Bruno Fernandes (${match.homeTeam.shortName})"
                    )
                    .forEach { goal ->
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                    imageVector = Icons.Default.Sports,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = goal, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
        }
    }
}

@Composable
private fun MatchLineupsContent(match: Match) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Home Team Lineup
        LineupCard(teamName = match.homeTeam.name, formation = "4-3-3", players = getSampleLineup())

        // Away Team Lineup
        LineupCard(
                teamName = match.awayTeam.name,
                formation = "4-2-3-1",
                players = getSampleLineup()
        )
    }
}

@Composable
private fun LineupCard(teamName: String, formation: String, players: List<MatchPlayer>) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                        text = teamName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )
                Text(
                        text = formation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            players.take(11).forEach { player -> PlayerLineupItem(player = player) }
        }
    }
}

@Composable
private fun PlayerLineupItem(player: MatchPlayer) {
    Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                    text = player.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
            )
            Text(
                    text = player.position,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
                text = player.rating.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun MatchStatsContent(match: Match) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Match Statistics Comparison
        val stats =
                listOf(
                        "Shots" to (12 to 8),
                        "Shots on Target" to (7 to 4),
                        "Possession" to (60 to 40),
                        "Pass Accuracy" to (85 to 78),
                        "Fouls" to (8 to 12),
                        "Corners" to (6 to 3)
                )

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                        text = "Match Statistics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )

                stats.forEach { (statName, values) ->
                    StatComparison(
                            statName = statName,
                            homeValue = values.first,
                            awayValue = values.second,
                            homeTeam = match.homeTeam.shortName ?: match.homeTeam.code ?: "Home",
                            awayTeam = match.awayTeam.shortName ?: match.awayTeam.code ?: "Away"
                    )
                }
            }
        }
    }
}

@Composable
private fun StatComparison(
        statName: String,
        homeValue: Int,
        awayValue: Int,
        homeTeam: String,
        awayTeam: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                    text = homeValue.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
            )
            Text(text = statName, style = MaterialTheme.typography.bodyMedium)
            Text(
                    text = awayValue.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
            )
        }

        // Progress bar showing comparison
        val total = homeValue + awayValue
        val homeProgress = if (total > 0) homeValue.toFloat() / total else 0f

        Row(modifier = Modifier.fillMaxWidth()) {
            LinearProgressIndicator(
                    progress = homeProgress,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun MatchTimelineContent(match: Match) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Sample timeline events
        val timelineEvents =
                listOf(
                        "90+3' Full Time",
                        "78' ⚽ Goal - Bruno Fernandes (${match.homeTeam.shortName})",
                        "65' 🔄 Substitution - ${match.awayTeam.shortName}",
                        "42' ⚽ Goal - Mohamed Salah (${match.awayTeam.shortName})",
                        "35' 🟨 Yellow Card - ${match.homeTeam.shortName}",
                        "15' ⚽ Goal - Marcus Rashford (${match.homeTeam.shortName})",
                        "0' Kick Off"
                )

        timelineEvents.forEach { event ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                Text(
                        text = event,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

private fun getSampleLineup(): List<MatchPlayer> {
    return listOf(
            MatchPlayer(1, "David de Gea", "GK", 7.2),
            MatchPlayer(2, "Aaron Wan-Bissaka", "RB", 6.8),
            MatchPlayer(3, "Raphael Varane", "CB", 7.5),
            MatchPlayer(4, "Harry Maguire", "CB", 6.9),
            MatchPlayer(5, "Luke Shaw", "LB", 7.1),
            MatchPlayer(6, "Casemiro", "CDM", 8.2),
            MatchPlayer(7, "Bruno Fernandes", "CAM", 8.5),
            MatchPlayer(8, "Paul Pogba", "CM", 7.0),
            MatchPlayer(9, "Marcus Rashford", "LW", 8.8),
            MatchPlayer(10, "Cristiano Ronaldo", "ST", 7.6),
            MatchPlayer(11, "Jadon Sancho", "RW", 7.3)
    )
}
