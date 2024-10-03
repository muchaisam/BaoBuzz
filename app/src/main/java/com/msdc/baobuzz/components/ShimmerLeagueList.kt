package com.msdc.baobuzz.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerLeagueList() {
    BubbleFlowRow {
        repeat(5) {
            ShimmerBubble()
        }
    }
}

@Composable
fun ShimmerTeamList() {
    BubbleFlowRow {
        repeat(12) {
            ShimmerBubble()
        }
    }
}

@Composable
fun ShimmerBubble() {
    Surface(
        modifier = Modifier
            .width(120.dp)
            .height(40.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {}
}