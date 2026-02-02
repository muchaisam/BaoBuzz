package com.msdc.baobuzz.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import coil.compose.*
import com.msdc.baobuzz.models.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(teamId: Int, onBackPressed: () -> Unit = {}, modifier: Modifier = Modifier) {
    // Mock team data - would come from ViewModel in real implementation
    val team by remember {
        mutableStateOf(
                Team(
                        id = teamId,
                        name = "Manchester United",
                        code = "MUN",
                        logo = "https://media-4.api-sports.io/football/teams/33.png",
                        founded = 1878,
                        country = "England",
                        national = false
                )
        )
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Squad", "Fixtures", "Stats")

    // Animation states
    val headerAlpha by
            animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = tween(800),
                    label = "header_alpha"
            )

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Enhanced Header with Team Info
        TeamDetailHeader(team = team, onBackPressed = onBackPressed, alpha = headerAlpha)

        // Tab Row
        ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 16.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight =
                                            if (selectedTab == index) FontWeight.Bold
                                            else FontWeight.Medium
                            )
                        }
                )
            }
        }

        // Tab Content with Animation
        AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(tween(300)) + slideInHorizontally { it / 4 } togetherWith
                            fadeOut(tween(150)) + slideOutHorizontally { -it / 4 }
                },
                label = "tab_content"
        ) { tabIndex ->
            when (tabIndex) {
                0 -> TeamOverviewTab(team = team)
                1 -> TeamSquadTab(teamId = teamId)
                2 -> TeamFixturesTab(teamId = teamId)
                3 -> TeamStatsTab(teamId = teamId)
            }
        }
    }
}

@Composable
private fun TeamDetailHeader(
        team: Team,
        onBackPressed: () -> Unit,
        alpha: Float,
        modifier: Modifier = Modifier
) {
    Box(
            modifier =
                    modifier.fillMaxWidth()
                            .height(220.dp)
                            .background(
                                    brush =
                                            Brush.verticalGradient(
                                                    colors =
                                                            listOf(
                                                                    MaterialTheme.colorScheme
                                                                            .primary,
                                                                    MaterialTheme.colorScheme.primary
                                                                            .copy(alpha = 0.8f)
                                                            )
                                            )
                            )
    ) {
        // Back Button
        IconButton(
                onClick = onBackPressed,
                modifier =
                        Modifier.padding(16.dp)
                                .background(
                                        color =
                                                MaterialTheme.colorScheme.surface.copy(
                                                        alpha = 0.9f
                                                ),
                                        shape = CircleShape
                                )
        ) {
            Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
            )
        }

        // Team Info
        Column(
                modifier = Modifier.align(Alignment.Center).alpha(alpha),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Team Logo with Animation
            val logoScale by
                    animateFloatAsState(
                            targetValue = 1f,
                            animationSpec =
                                    spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                    ),
                            label = "logo_scale"
                    )

            AsyncImage(
                    model = team.logo,
                    contentDescription = "${team.name} logo",
                    modifier =
                            Modifier.size(80.dp)
                                    .scale(logoScale)
                                    .background(
                                            color = MaterialTheme.colorScheme.surface,
                                            shape = CircleShape
                                    )
                                    .padding(12.dp)
                                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                    text = team.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                        text = team.country,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )

                Text(text = "•", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))

                Text(
                        text = "Founded ${team.founded ?: "N/A"}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )
            }
        }

        // Favorite Button
        FloatingActionButton(
                onClick = { /* Toggle favorite */},
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).size(48.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.tertiary
        ) {
            Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Add to favorites",
                    modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun TeamOverviewTab(team: Team, modifier: Modifier = Modifier) {
    LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { TeamStatsCard() }

        item { RecentFormCard() }

        item { NextMatchCard() }

        item { TeamInfoCard(team = team) }
    }
}

@Composable
private fun TeamStatsCard(modifier: Modifier = Modifier) {
    Card(
            modifier = modifier.fillMaxWidth(),
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                        text = "Season Stats",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                )

                Icon(
                        imageVector = Icons.Outlined.BarChart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "Position", value = "3rd", icon = Icons.Default.EmojiEvents)

                StatItem(label = "Points", value = "67", icon = Icons.Default.Star)

                StatItem(label = "Goals", value = "78", icon = Icons.Default.Sports)
            }
        }
    }
}

@Composable
private fun StatItem(
        label: String,
        value: String,
        icon: ImageVector,
        modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
        )

        Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RecentFormCard(modifier: Modifier = Modifier) {
    Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                    text = "Recent Form",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val results = listOf("W", "W", "L", "D", "W")

                results.forEach { result -> FormResultBadge(result = result) }
            }
        }
    }
}

@Composable
private fun FormResultBadge(result: String, modifier: Modifier = Modifier) {
    val backgroundColor =
            when (result) {
                "W" -> Color(0xFF4CAF50)
                "L" -> Color(0xFFF44336)
                "D" -> Color(0xFFFF9800)
                else -> Color.Gray
            }

    Box(
            modifier =
                    modifier.size(36.dp).background(color = backgroundColor, shape = CircleShape),
            contentAlignment = Alignment.Center
    ) {
        Text(
                text = result,
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun NextMatchCard(modifier: Modifier = Modifier) {
    Card(
            modifier = modifier.fillMaxWidth(),
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                    ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                    text = "Next Match",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                        text = "vs Liverpool",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                            text = "Sunday, 15:00",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                            text = "Old Trafford",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamInfoCard(team: Team, modifier: Modifier = Modifier) {
    Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                    text = "Team Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoRow(label = "Full Name", value = team.name)
            InfoRow(label = "Founded", value = team.founded?.toString() ?: "N/A")
            InfoRow(label = "Country", value = team.country)
            InfoRow(label = "Stadium", value = "Old Trafford")
            InfoRow(label = "Capacity", value = "74,310")
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
            modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TeamSquadTab(teamId: Int, modifier: Modifier = Modifier) {
    LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                    text = "Squad coming soon...",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun TeamFixturesTab(teamId: Int, modifier: Modifier = Modifier) {
    LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                    text = "Fixtures coming soon...",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun TeamStatsTab(teamId: Int, modifier: Modifier = Modifier) {
    LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                    text = "Detailed stats coming soon...",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
            )
        }
    }
}
