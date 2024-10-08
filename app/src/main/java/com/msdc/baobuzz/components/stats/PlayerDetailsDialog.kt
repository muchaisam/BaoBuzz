package com.msdc.baobuzz.components.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.msdc.baobuzz.models.PlayerStats
import com.msdc.baobuzz.ux.Typography

@Composable
fun PlayerDetailsDialog(
    player: PlayerStats,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = player.player.name, style = Typography.h5)
        },
        text = {
            Column {
                Typography.body1
                AsyncImage(
                    model = player.player.photo,
                    contentDescription = "Player photo",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Age: ${player.player.age}", style = Typography.body1)
                Text("Nationality: ${player.player.nationality}", style = Typography.body1)
                Text("Height: ${player.player.height}", style = Typography.body1)
                Text("Weight: ${player.player.weight}" , style = Typography.body1)
                player.statistics.firstOrNull()?.let { stats ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Team: ${stats.team.name}" , style = Typography.body1)
                    Text("Goals: ${stats.goals.total ?: 0}" , style = Typography.body1)
                    Text("Assists: ${stats.goals.assists ?: 0}" , style = Typography.body1)
                    Text("Appearances: ${stats.games.appearances ?: 0}" , style = Typography.body1)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}