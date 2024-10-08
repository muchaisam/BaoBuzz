package com.msdc.baobuzz.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SelectedLeaguesSummary(
    selectedLeagueCount: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = "Selected $selectedLeagueCount ${if (selectedLeagueCount == 1) "league" else "leagues"}",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(12.dp)
        )
    }
}
