package com.msdc.baobuzz.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.msdc.baobuzz.viewmodel.LeagueSelectionViewModel

@Composable
fun NotificationSetupStep(viewModel: LeagueSelectionViewModel) {
    val selectedTeams by viewModel.selectedTeams.collectAsState()
    val teams by viewModel.teams.collectAsState()
    val notifications by viewModel.teamNotifications.collectAsState()

    LazyColumn {
        items(teams.filter { selectedTeams.contains(it.id) }) { team ->
            NotificationToggle(
                team = team,
                isEnabled = notifications[team.id] ?: false,
                onToggle = { viewModel.toggleTeamNotification(team.id) }
            )
        }
    }
}