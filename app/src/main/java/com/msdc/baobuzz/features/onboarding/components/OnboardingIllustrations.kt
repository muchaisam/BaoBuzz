package com.msdc.baobuzz.features.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msdc.baobuzz.ui.theme.BaoBuzzColors
import com.msdc.baobuzz.ui.theme.rememberFloatingAnimation

@Composable
fun LiveScoreIllustration(modifier: Modifier = Modifier) {
    val floatingOffset = rememberFloatingAnimation(distance = 6f, durationMillis = 3000)

    Box(
        modifier = modifier.graphicsLayer(translationY = floatingOffset),
        contentAlignment = Alignment.Center
    ) {
        // Glassmorphic match card
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.08f),
                            Color.White.copy(alpha = 0.03f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.2f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // "LIVE" badge
                LiveBadge()

                Spacer(modifier = Modifier.height(16.dp))

                // Teams and score
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Home team
                    TeamPlaceholder(name = "HOME")

                    // Score
                    ScoreDisplay()

                    // Away team
                    TeamPlaceholder(name = "AWAY")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Match time
                Text(
                    text = "67'",
                    style = MaterialTheme.typography.bodySmall,
                    color = BaoBuzzColors.AccentOrange.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun LiveBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BaoBuzzColors.AccentOrange.copy(alpha = 0.2f))
            .border(
                width = 1.dp,
                color = BaoBuzzColors.AccentOrange.copy(alpha = 0.4f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = "LIVE",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            ),
            color = BaoBuzzColors.AccentOrange
        )
    }
}

@Composable
private fun TeamPlaceholder(name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun ScoreDisplay() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "2",
            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Text(
            text = " - ",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White.copy(alpha = 0.5f)
        )
        Text(
            text = "1",
            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
    }
}

@Composable
fun StandingsIllustration(modifier: Modifier = Modifier) {
    val floatingOffset = rememberFloatingAnimation(distance = 5f, durationMillis = 3500)

    Box(
        modifier = modifier.graphicsLayer(translationY = floatingOffset),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.08f),
                            Color.White.copy(alpha = 0.03f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.2f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                // Table header
                StandingsHeaderRow()
                Spacer(modifier = Modifier.height(12.dp))

                // 3 team rows
                StandingsTeamRow(rank = 1, points = 72, barWidth = 0.9f, color = BaoBuzzColors.AccentGreen)
                Spacer(modifier = Modifier.height(10.dp))
                StandingsTeamRow(rank = 2, points = 68, barWidth = 0.85f, color = BaoBuzzColors.PrimaryBlueLight)
                Spacer(modifier = Modifier.height(10.dp))
                StandingsTeamRow(rank = 3, points = 61, barWidth = 0.75f, color = BaoBuzzColors.InfoCyan)
            }
        }
    }
}

@Composable
private fun StandingsHeaderRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "STANDINGS",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = Color.White.copy(alpha = 0.5f)
        )
        Text(
            text = "PTS",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun StandingsTeamRow(rank: Int, points: Int, barWidth: Float, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$rank",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.width(20.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.width(8.dp))
        // Team badge placeholder
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f))
                .border(1.dp, color.copy(alpha = 0.4f), CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        // Points bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(alpha = 0.06f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(barWidth)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(color, color.copy(alpha = 0.4f))
                        )
                    )
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "$points",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
    }
}
