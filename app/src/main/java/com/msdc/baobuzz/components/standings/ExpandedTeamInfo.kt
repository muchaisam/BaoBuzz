package com.msdc.baobuzz.components.standings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.msdc.baobuzz.models.TeamStanding
import com.msdc.baobuzz.ux.Typography

@Composable
fun ExpandedTeamInfo(standing: TeamStanding) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Recent Form",
            style = Typography.body2
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
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
        // Add more expanded info here (e.g., top scorer, upcoming matches)
    }
}