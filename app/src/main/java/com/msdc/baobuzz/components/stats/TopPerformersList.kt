package com.msdc.baobuzz.components.stats


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.msdc.baobuzz.models.PlayerStats
import com.msdc.baobuzz.ux.Typography

@Composable
fun PlayerCard(
    player: PlayerStats,
    statValue: Int,
    statName: String,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val surfaceColor = if (isDarkTheme) Color.DarkGray else Color.White
    val onSurfaceColor = if (isDarkTheme) Color.White else Color.Black
    val secondaryContainerColor = if (isDarkTheme) Color(0xFF2C3E50) else Color(0xFFECF0F1)
    val onSecondaryContainerColor = if (isDarkTheme) Color.White else Color.Black

    Card(
        modifier = Modifier
            .size(width = 150.dp, height = 230.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = 6.dp,
        backgroundColor = surfaceColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                model = player.player.photo,
                contentDescription = "Player photo",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = player.player.name,
                    style = Typography.h5,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    color = onSurfaceColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = player.statistics.firstOrNull()?.team?.name ?: "Unknown",
                    style = Typography.body2,
                    color = onSurfaceColor.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Surface(
                color = secondaryContainerColor,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$statValue",
                        style = Typography.body1,
                        color = onSecondaryContainerColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = statName,
                        style = Typography.body2,
                        color = onSecondaryContainerColor
                    )
                }
            }
        }
    }
}

@Composable
fun TopScorersList(
    players: List<PlayerStats>?,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedPlayer by remember { mutableStateOf<PlayerStats?>(null) }

    Column(modifier = modifier) {
        Text(
            text = "Top Scorers",
            style = Typography.body1,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(players?.take(10) ?: emptyList()) { player ->
                PlayerCard(
                    player = player,
                    statValue = player.statistics.firstOrNull()?.goals?.total ?: 0,
                    statName = "Goals",
                    onClick = {
                        selectedPlayer = player
                        showDialog = true
                    }
                )
            }
        }
    }

    if (showDialog && selectedPlayer != null) {
        PlayerDetailsDialog(
            player = selectedPlayer!!,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun TopAssistersList(
    players: List<PlayerStats>?,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedPlayer by remember { mutableStateOf<PlayerStats?>(null) }

    Column(modifier = modifier) {
        Text(
            text = "Top Assisters",
            style = Typography.body1,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(players?.take(10) ?: emptyList()) { player ->
                PlayerCard(
                    player = player,
                    statValue = player.statistics.firstOrNull()?.goals?.assists ?: 0,
                    statName = "Assists",
                    onClick = {
                        selectedPlayer = player
                        showDialog = true
                    }
                )
            }
        }
    }

    if (showDialog && selectedPlayer != null) {
        PlayerDetailsDialog(
            player = selectedPlayer!!,
            onDismiss = { showDialog = false }
        )
    }
}

