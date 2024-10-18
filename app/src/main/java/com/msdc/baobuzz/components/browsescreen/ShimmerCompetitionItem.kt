package com.msdc.baobuzz.components.browsescreen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerCompetitionItem() {
    val transition = rememberInfiniteTransition()
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f)
        ),
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(brush)
        )
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Spacer(
                modifier = Modifier
                    .height(12.dp)
                    .width(100.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Spacer(
                modifier = Modifier
                    .height(16.dp)
                    .width(150.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
        }
    }
}