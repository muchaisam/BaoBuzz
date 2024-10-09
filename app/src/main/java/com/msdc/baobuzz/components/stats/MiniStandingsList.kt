package com.msdc.baobuzz.components.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.msdc.baobuzz.models.LeagueStandings
import com.msdc.baobuzz.models.TeamStanding
import com.msdc.baobuzz.ux.Typography

@Composable
fun MiniStandingsList(
    standings: LeagueStandings?,
    onViewFullStandings: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 300.dp)
            .padding(16.dp),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Standings",
                style = Typography.h5,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            standings?.league?.standings?.firstOrNull()?.take(5)?.forEach { standing ->
                MiniStandingItem(standing)
            }
        }
    }
}

@Composable
fun MiniStandingItem(standing: TeamStanding) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${standing.rank}.",
            modifier = Modifier.width(30.dp),
            style = Typography.body2
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
            style = Typography.body2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${standing.points}",
            style = Typography.body2,
            fontWeight = FontWeight.Bold
        )
    }
}
