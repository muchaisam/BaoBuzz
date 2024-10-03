package com.msdc.baobuzz.ui


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import com.msdc.baobuzz.components.NavigationButtons
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        LinearProgressIndicator(
            progress = { (currentStep + 1) / steps.size.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        AnimatedContent(
            targetState = steps[currentStep],
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
            },
            modifier = Modifier.padding(16.dp)
        ) { stepTitle ->
            Text(
                text = stepTitle,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300))
                }
            ) { step ->
                when (step) {
                    0 -> LeagueSelectionStep(viewModel)
                    1 -> TeamSelectionStep(viewModel)
                    2 -> NotificationSetupStep(viewModel)
                }
            }
        }

        NavigationButtons(
            currentStep = currentStep,
            totalSteps = steps.size,
            onPrevious = { currentStep-- },
            onNext = {
                if (currentStep < steps.size - 1) currentStep++
                else {
                    viewModel.savePreferences()
                    onComplete()
                }
            }
        )
    }
}



@Composable
fun EmptyMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}