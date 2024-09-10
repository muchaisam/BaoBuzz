package com.example.baobuzz.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.baobuzz.models.LeagueStanding
import com.example.baobuzz.models.LeagueStandings
import com.example.baobuzz.models.StandingsUiState
import com.example.baobuzz.models.StandingsViewModel
import com.example.baobuzz.models.TeamStanding

@Composable
fun StandingsScreen(viewModel: StandingsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedLeague by viewModel.selectedLeague.collectAsState()

    Column {
        when (val state = uiState) {
            is StandingsUiState.Loading -> LoadingScreen()
            is StandingsUiState.Success -> {
                LeagueSelector(
                    leagues = state.standings.keys.toList(),
                    selectedLeague = selectedLeague,
                    onLeagueSelected = { viewModel.selectLeague(it) }
                )
                StandingsList(standings = state.standings[selectedLeague])
            }
            is StandingsUiState.Error -> ErrorScreen(state.message) { viewModel.retry() }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LeagueSelector(
    leagues: List<Int>,
    selectedLeague: Int,
    onLeagueSelected: (Int) -> Unit
) {
    val leagueInfo = mapOf(
        39 to Pair("Premier League", "https://example.com/premier_league.png"),
        140 to Pair("La Liga", "https://example.com/la_liga.png"),
        61 to Pair("Ligue 1", "https://example.com/ligue_1.png"),
        78 to Pair("Bundesliga", "https://example.com/bundesliga.png"),
        135 to Pair("Serie A", "https://example.com/serie_a.png")
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        items(leagues) { leagueId ->
            val (name, logo) = leagueInfo[leagueId] ?: Pair("Unknown", "")
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Card(
                    onClick = { onLeagueSelected(leagueId) },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(80.dp),
                    elevation = if (leagueId == selectedLeague) 8.dp else 2.dp,
                    backgroundColor = if (leagueId == selectedLeague) MaterialTheme.colors.primary else MaterialTheme.colors.surface
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = logo,
                            contentDescription = "$name logo",
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = name,
                            style = MaterialTheme.typography.caption,
                            color = if (leagueId == selectedLeague) Color.White else MaterialTheme.colors.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        FootballLoadingAnimation()
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StandingsList(standings: LeagueStandings?) {
    LazyColumn {
        item {
            standings?.league?.let { league ->
                LeagueHeader(league)
            }
        }

        stickyHeader {
            StandingsLegend()
        }

        standings?.league?.standings?.firstOrNull()?.let { teamStandings ->
            itemsIndexed(teamStandings) { index, standing ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(
                        initialOffsetY = { it * (index + 1) } // staggered animation
                    )
                ) {
                    StandingItem(standing)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp)) // Add extra space at the bottom
        }
    }
}

@Composable
fun LeagueHeader(league: LeagueStanding) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = league.logo,
            contentDescription = "League logo",
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = league.name,
            style = MaterialTheme.typography.h5
        )
    }
}

@Composable
fun StandingsLegend() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Pos",
            modifier = Modifier.width(30.dp),
            style = MaterialTheme.typography.caption,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Team",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.caption,
            fontWeight = FontWeight.Bold
        )
        StatsLegend()
    }
}

@Composable
fun StatsLegend() {
    Row {
        listOf("P", "W", "D", "L", "GD", "Pts").forEach { stat ->
            Text(
                text = stat,
                modifier = Modifier.width(30.dp),
                style = MaterialTheme.typography.caption,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StandingItem(standing: TeamStanding) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = 2.dp,
        onClick = { expanded = !expanded }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${standing.rank}.",
                    modifier = Modifier.width(30.dp),
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Bold
                )
                AsyncImage(
                    model = standing.team.logo,
                    contentDescription = "Team logo",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = standing.team.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.body2
                )
                StandingStats(standing)
            }
            if (expanded) {
                Divider()
                ExpandedTeamInfo(standing)
            }
        }
    }
}

@Composable
fun StandingStats(standing: TeamStanding) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "${standing.all.played}",
            modifier = Modifier.width(30.dp),
            style = MaterialTheme.typography.body2
        )
        Text(
            text = "${standing.all.win}",
            modifier = Modifier.width(30.dp),
            style = MaterialTheme.typography.body2
        )
        Text(
            text = "${standing.all.draw}",
            modifier = Modifier.width(30.dp),
            style = MaterialTheme.typography.body2
        )
        Text(
            text = "${standing.all.lose}",
            modifier = Modifier.width(30.dp),
            style = MaterialTheme.typography.body2
        )
        Text(
            text = "${standing.goalsDiff}",
            modifier = Modifier.width(30.dp),
            style = MaterialTheme.typography.body2,
            color = when {
                standing.goalsDiff > 0 -> Color.Green
                standing.goalsDiff < 0 -> Color.Red
                else -> MaterialTheme.colors.onSurface
            }
        )
        Text(
            text = "${standing.points}",
            modifier = Modifier.width(30.dp),
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ExpandedTeamInfo(standing: TeamStanding) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Recent Form",
            style = MaterialTheme.typography.subtitle2
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row {
            standing.form.take(5).forEach { result ->
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            when (result) {
                                'W' -> Color.Green
                                'D' -> Color.Yellow
                                'L' -> Color.Red
                                else -> Color.Gray
                            },
                            shape = MaterialTheme.shapes.small
                        )
                ) {
                    Text(
                        text = result.toString(),
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White,
                        style = MaterialTheme.typography.caption
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
        // Add more expanded info here (e.g., top scorer, upcoming matches)
    }
}


@Composable
fun FootballLoadingAnimation() {
    var rotation by remember { mutableStateOf(0f) }
    val rotationAnim = rememberInfiniteTransition()
    rotation = rotationAnim.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    ).value

    Box(
        modifier = Modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        // Football
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color.White, shape = CircleShape)
                .border(2.dp, Color.Black, CircleShape)
        )
        // Pentagons
        repeat(5) { index ->
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .offset(y = (-20).dp)
                    .rotate(rotation + (index * 72))
                    .graphicsLayer {
                        shape = androidx.compose.ui.graphics.RectangleShape
                        clip = true
                    }
                    .background(Color.Black)
            )
        }
    }
}


fun firstWordAbbreviation(name: String): String {
    return name.split(" ").first().take(3).uppercase()
}