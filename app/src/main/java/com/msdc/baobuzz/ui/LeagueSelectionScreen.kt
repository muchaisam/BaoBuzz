package com.msdc.baobuzz.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.msdc.baobuzz.components.LeagueSelectionStep
import com.msdc.baobuzz.components.NotificationSetupStep
import com.msdc.baobuzz.components.TeamSelectionStep
import com.msdc.baobuzz.viewmodel.LeagueSelectionViewModel

@Composable
fun LeagueSelectionScreen(
    viewModel: LeagueSelectionViewModel,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableStateOf(0) }
    val steps = listOf("Select Leagues", "Select Teams", "Set Notifications")

    Column(modifier = modifier.fillMaxSize()) {
        LinearProgressIndicator(
            progress = { (currentStep + 1) / steps.size.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Text(
            text = steps[currentStep],
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        when (currentStep) {
            0 -> LeagueSelectionStep(viewModel)
            1 -> TeamSelectionStep(viewModel)
            2 -> NotificationSetupStep(viewModel)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentStep > 0) {
                Button(onClick = { currentStep-- }) {
                    Text("Previous")
                }
            }
            Button(
                onClick = {
                    if (currentStep < steps.size - 1) currentStep++
                    else {
                        viewModel.savePreferences()
                        onComplete()
                    }
                }
            ) {
                Text(if (currentStep == steps.size - 1) "Finish" else "Next")
            }
        }
    }
}



@Composable
fun EmptyTeamsMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select a league first to see teams",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}