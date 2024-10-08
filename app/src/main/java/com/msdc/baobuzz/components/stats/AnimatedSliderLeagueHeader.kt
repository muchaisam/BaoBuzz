package com.msdc.baobuzz.components.stats

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlin.math.roundToInt

@Composable
fun AnimatedSliderLeagueHeader(
    leagues: List<Int>,
    selectedLeague: Int,
    onLeagueSelected: (Int) -> Unit,
    leagueInfo: Map<Int, Pair<String, String>>
) {
    val selectedIndex = leagues.indexOf(selectedLeague)
    val sliderPosition = remember { Animatable(selectedIndex.toFloat()) }

    LaunchedEffect(selectedLeague) {
        sliderPosition.animateTo(
            targetValue = selectedIndex.toFloat(),
            animationSpec = spring(stiffness = Spring.StiffnessLow)
        )
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        // Background slider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        // Animated highlight
        Box(
            modifier = Modifier
                .offset { IntOffset(((sliderPosition.value * 100).roundToInt()), 0) }
                .width(100.dp)
                .height(80.dp)
                .background(MaterialTheme.colorScheme.primaryContainer)
        )

        // League items
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            leagues.forEachIndexed { index, leagueId ->
                val (name, logo) = leagueInfo[leagueId] ?: ("Unknown" to "")
                Column(
                    modifier = Modifier
                        .width(100.dp)
                        .height(80.dp)
                        .clickable { onLeagueSelected(leagueId) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = logo,
                        contentDescription = name,
                        modifier = Modifier.size(40.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}