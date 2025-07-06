package com.msdc.baobuzz.features.leagues

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.msdc.baobuzz.core.models.Fixture
import com.msdc.baobuzz.core.models.LeagueStanding
import com.msdc.baobuzz.core.models.Transfer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaguesScreen(viewModel: LeaguesViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Leagues",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = { viewModel.retry() }) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {
            is LeaguesUiState.Loading -> {
                LoadingState()
            }

            is LeaguesUiState.Error -> {
                ErrorState(message = state.message, onRetry = { viewModel.retry() })
            }

            is LeaguesUiState.Success -> {
                SuccessState(
                    standings = state.standings,
                    fixtures = state.fixtures,
                    transfers = state.transfers
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading league standings...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Error loading leagues",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) { Text("Try Again") }
        }
    }
}

@Composable
private fun SuccessState(
    standings: List<LeagueStanding>,
    fixtures: List<Fixture>,
    transfers: List<Transfer>
) {
    if (standings.isEmpty()) {
        EmptyState(
            title = "No Leagues Selected",
            description =
            "Please select some leagues in your preferences to see standings and data."
        )
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // League Standings Section
            item {
                Text(
                    text = "League Standings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(standings) { standing -> LeagueStandingsCard(standing = standing) }

            // Upcoming Fixtures Section
            if (fixtures.isNotEmpty()) {
                item {
                    Text(
                        text = "Upcoming Fixtures",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }

                items(fixtures.take(5)) { fixture -> FixtureCard(fixture = fixture) }
            }

            // Recent Transfers Section
            if (transfers.isNotEmpty()) {
                item {
                    Text(
                        text = "Recent Transfers",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }

                items(transfers.take(5)) { transfer -> TransferCard(transfer = transfer) }
            }
        }
    }
}

@Composable
private fun EmptyState(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.List,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FixtureCard(fixture: Fixture) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = fixture.homeTeam.logo,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = fixture.homeTeam.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "vs",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = fixture.awayTeam.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AsyncImage(
                        model = fixture.awayTeam.logo,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = fixture.venue,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = fixture.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TransferCard(transfer: Transfer) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = transfer.player.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (transfer.fromTeam != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = transfer.fromTeam.logo,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = transfer.fromTeam.name,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    Text(
                        text = "Free Agent",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "transferred to",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = transfer.toTeam.logo,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = transfer.toTeam.name, style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = transfer.date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LeagueStandingsCard(standing: LeagueStanding) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // League Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = standing.leagueLogo,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = standing.leagueName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Standings Table Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Pos",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(32.dp)
                )
                Text(
                    text = "Team",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Pts",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(32.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "P",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(24.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "W",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(24.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "D",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(24.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "L",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(24.dp),
                    textAlign = TextAlign.Center
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Top teams (show top 10)
            standing.topTeams.take(10).forEach { teamStanding ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Position
                    Card(
                        colors =
                        CardDefaults.cardColors(
                            containerColor =
                            when (teamStanding.position) {
                                1 ->
                                    MaterialTheme.colorScheme
                                        .primaryContainer

                                in 2..4 ->
                                    MaterialTheme.colorScheme
                                        .secondaryContainer

                                in 18..20 ->
                                    MaterialTheme.colorScheme
                                        .errorContainer

                                else -> MaterialTheme.colorScheme.surface
                            }
                        ),
                        modifier = Modifier.width(32.dp)
                    ) {
                        Text(
                            text = teamStanding.position.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(4.dp)
                        )
                    }

                    // Team
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        AsyncImage(
                            model = teamStanding.team.logo,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = teamStanding.team.name,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1
                        )
                    }

                    // Points
                    Text(
                        text = teamStanding.points.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(32.dp),
                        textAlign = TextAlign.Center
                    )

                    // Played
                    Text(
                        text = teamStanding.played.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.width(24.dp),
                        textAlign = TextAlign.Center
                    )

                    // Won
                    Text(
                        text = teamStanding.won.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.width(24.dp),
                        textAlign = TextAlign.Center
                    )

                    // Drawn
                    Text(
                        text = teamStanding.drawn.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.width(24.dp),
                        textAlign = TextAlign.Center
                    )

                    // Lost
                    Text(
                        text = teamStanding.lost.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.width(24.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (standing.topTeams.size > 10) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Showing top 10 teams",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
