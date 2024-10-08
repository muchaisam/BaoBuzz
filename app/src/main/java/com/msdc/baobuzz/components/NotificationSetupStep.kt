package com.msdc.baobuzz.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.msdc.baobuzz.viewmodel.LeagueSelectionViewModel

@Composable
fun NotificationSetupStep(
    viewModel: LeagueSelectionViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTeams by viewModel.selectedTeams.collectAsState()
    val teams by viewModel.teams.collectAsState()
    val notifications by viewModel.teamNotifications.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Choose which teams to get notifications for:",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(
            items = teams.filter { selectedTeams.contains(it.id) }
                .chunked(2)
        ) { teamPair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                teamPair.forEach { team ->
                    NotificationToggleItem(
                        team = team,
                        isEnabled = notifications[team.id] ?: false,
                        onToggle = { viewModel.toggleTeamNotification(team.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (teamPair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}