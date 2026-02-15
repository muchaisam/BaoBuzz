package com.msdc.baobuzz.features.onboarding.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msdc.baobuzz.R
import com.msdc.baobuzz.ui.theme.glassRadial
import com.msdc.baobuzz.ui.theme.rememberBreathingAnimation
import kotlinx.coroutines.delay

@Composable
fun OnboardingWelcomePage(isActive: Boolean) {
    val breathingScale = rememberBreathingAnimation(minScale = 0.95f, maxScale = 1.05f, durationMillis = 3000)

    var showTitle by remember { mutableStateOf(false) }
    var showSubtitle by remember { mutableStateOf(false) }

    LaunchedEffect(isActive) {
        if (isActive) {
            delay(200)
            showTitle = true
            delay(200)
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
        // Logo with breathing animation and glass radial glow
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .glassRadial(
                    centerColor = Color.White,
                    edgeColor = Color.Transparent,
                    alpha = 0.12f
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "BaoBuzz Logo",
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer(scaleX = breathingScale, scaleY = breathingScale),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        AnimatedVisibility(
            visible = showTitle,
            enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { 30 }
        ) {
            Text(
                text = "Welcome to\nBaoBuzz",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 38.sp,
                    lineHeight = 44.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = showSubtitle,
            enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { 20 }
        ) {
            Text(
                text = "Your ultimate football companion",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp),
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}
