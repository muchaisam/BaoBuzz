package com.msdc.baobuzz.features.onboarding.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.msdc.baobuzz.ui.theme.BaoBuzzColors

@Composable
fun OnboardingDotIndicator(
    totalDots: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalDots) { index ->
            OnboardingDot(isSelected = index == selectedIndex)
        }
    }
}

@Composable
private fun OnboardingDot(isSelected: Boolean) {
    val width by animateDpAsState(
        targetValue = if (isSelected) 28.dp else 8.dp,
        animationSpec = spring(dampingRatio = 0.7f),
        label = "dot_width"
    )
    val color by animateColorAsState(
        targetValue = if (isSelected) BaoBuzzColors.PrimaryBlueLight else Color.White.copy(alpha = 0.3f),
        label = "dot_color"
    )

    Box(
        modifier = Modifier
            .width(width)
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color)
    )
}
