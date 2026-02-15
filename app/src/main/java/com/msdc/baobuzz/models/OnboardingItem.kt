package com.msdc.baobuzz.models

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable

// ✅ @Immutable for Compose performance
@Immutable
data class OnboardingItem(
    @DrawableRes val imageRes: Int,
    val title: String,
    val description: String
)