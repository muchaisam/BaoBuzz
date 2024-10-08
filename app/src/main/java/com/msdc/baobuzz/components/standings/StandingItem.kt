package com.msdc.baobuzz.components.standings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material3.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.msdc.baobuzz.models.TeamStanding
import com.msdc.baobuzz.ux.Typography

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
                    style = Typography.body2,
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
                    style = Typography.body2
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