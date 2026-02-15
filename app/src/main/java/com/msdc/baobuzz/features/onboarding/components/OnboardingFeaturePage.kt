package com.msdc.baobuzz.features.onboarding.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun OnboardingFeaturePage(
    title: String,
    subtitle: String,
    isActive: Boolean,
    illustration: @Composable () -> Unit
) {
    var showIllustration by remember { mutableStateOf(false) }
    var showTitle by remember { mutableStateOf(false) }
    var showSubtitle by remember { mutableStateOf(false) }

    LaunchedEffect(isActive) {
        if (isActive) {
            showIllustration = true
            delay(200)
            showTitle = true
            delay(150)
            showSubtitle = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Illustration area
        AnimatedVisibility(
            visible = showIllustration,
            enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { 40 }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentAlignment = Alignment.Center
            ) {
                illustration()
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Title
        AnimatedVisibility(
            visible = showTitle,
            enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { 20 }
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Subtitle
        AnimatedVisibility(
            visible = showSubtitle,
            enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { 15 }
        ) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                color = Color.White.copy(alpha = 0.65f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }
    }
}
