package com.msdc.baobuzz.components.standings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.msdc.baobuzz.models.LeagueStanding
import com.msdc.baobuzz.models.LeagueStandings
import com.msdc.baobuzz.ux.Typography

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
            style = Typography.h5
        )
    }
}