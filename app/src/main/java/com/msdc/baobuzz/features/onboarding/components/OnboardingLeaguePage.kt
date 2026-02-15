package com.msdc.baobuzz.features.onboarding.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msdc.baobuzz.core.data.LeagueData
import kotlinx.coroutines.delay

@Composable
fun OnboardingLeaguePage(
    leagues: List<LeagueData.OnboardingLeague>,
    selectedIds: Set<Int>,
    onToggle: (Int) -> Unit,
    isActive: Boolean
) {
    var showHeader by remember { mutableStateOf(false) }
    var showList by remember { mutableStateOf(false) }

    LaunchedEffect(isActive) {
        if (isActive) {
            delay(100)
            showHeader = true
            delay(200)
            showList = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header
        AnimatedVisibility(
            visible = showHeader,
            enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { 20 }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Choose Your Leagues",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Follow the leagues you love",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // League list
        AnimatedVisibility(
            visible = showList,
            enter = fadeIn(tween(400)),
            modifier = Modifier.weight(1f)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = leagues,
                    key = { it.league.id }
                ) { onboardingLeague ->
                    GlassmorphicLeagueCard(
                        onboardingLeague = onboardingLeague,
                        isSelected = selectedIds.contains(onboardingLeague.league.id),
                        onToggle = { onToggle(onboardingLeague.league.id) }
                    )
                }
            }
        }
    }
}
