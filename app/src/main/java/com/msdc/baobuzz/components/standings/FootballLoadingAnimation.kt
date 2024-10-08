package com.msdc.baobuzz.components.standings

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay


@Composable
fun FootballLoadingAnimation() {
    var bounceState by remember { mutableStateOf(true) }
    val transition = updateTransition(targetState = bounceState, label = "Bounce")

    val yOffset by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                spring(stiffness = Spring.StiffnessLow)
            } else {
                spring(stiffness = Spring.StiffnessMedium)
            }
        },
        label = "Y Offset"
    ) { state ->
        if (state) 0f else -100f
    }

    val rotation by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 500)
        },
        label = "Rotation"
    ) { state ->
        if (state) 0f else 360f
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(800)
            bounceState = !bounceState
        }
    }

    Box(
        modifier = Modifier
            .size(100.dp)
            .offset(y = yOffset.dp)
            .rotate(rotation),
        contentAlignment = Alignment.Center
    ) {
        // Football
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, Color.LightGray),
                        center = Offset(0f, 0f),
                        radius = 30f
                    ),
                    shape = CircleShape
                )
                .border(2.dp, Color.Black, CircleShape)
        )
        // Pentagons
        repeat(5) { index ->
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .offset(y = (-20).dp)
                    .rotate(index * 72f)
                    .background(Color.Black, MaterialTheme.shapes.medium)
            )
        }
    }
}