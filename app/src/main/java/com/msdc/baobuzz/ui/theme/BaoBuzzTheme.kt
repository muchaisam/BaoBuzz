package com.msdc.baobuzz.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Professional Sports App Color Palette
object BaoBuzzColors {
    // Primary Brand Colors
    val PrimaryBlue = Color(0xFF1E40AF) // Rich blue for primary actions
    val PrimaryBlueLight = Color(0xFF3B82F6) // Lighter blue for gradients
    val AccentOrange = Color(0xFFF97316) // Vibrant orange for live content
    val AccentGreen = Color(0xFF10B981) // Success green for wins/positive stats

    // Semantic Colors
    val ErrorRed = Color(0xFFEF4444) // Losses, errors, alerts
    val WarningAmber = Color(0xFFF59E0B) // Draws, warnings, cautions
    val InfoCyan = Color(0xFF06B6D4) // Information, neutral stats

    // Surface & Background Colors
    val SurfaceLight = Color(0xFFF8FAFC) // Light mode card backgrounds
    val SurfaceDark = Color(0xFF1E293B) // Dark mode card backgrounds
    val BackgroundLight = Color(0xFFFFFFFF) // Light mode main background
    val BackgroundDark = Color(0xFF0F172A) // Dark mode main background
    val SurfaceVariantLight = Color(0xFFF1F5F9) // Light mode secondary surfaces
    val SurfaceVariantDark = Color(0xFF334155) // Dark mode secondary surfaces

    // Content Colors
    val OnSurfaceLight = Color(0xFF1E293B) // Light mode primary text
    val OnSurfaceDark = Color(0xFFF8FAFC) // Dark mode primary text
    val OnSurfaceVariantLight = Color(0xFF64748B) // Light mode secondary text
    val OnSurfaceVariantDark = Color(0xFF94A3B8) // Dark mode secondary text
}

private val DarkColorScheme =
    darkColorScheme(
        primary = BaoBuzzColors.PrimaryBlue,
        secondary = BaoBuzzColors.AccentOrange,
        tertiary = BaoBuzzColors.AccentGreen,
        error = BaoBuzzColors.ErrorRed,
        background = BaoBuzzColors.BackgroundDark,
        surface = BaoBuzzColors.SurfaceDark,
        surfaceVariant = BaoBuzzColors.SurfaceVariantDark,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onTertiary = Color.White,
        onError = Color.White,
        onBackground = BaoBuzzColors.OnSurfaceDark,
        onSurface = BaoBuzzColors.OnSurfaceDark,
        onSurfaceVariant = BaoBuzzColors.OnSurfaceVariantDark,
        primaryContainer = BaoBuzzColors.PrimaryBlueLight,
        secondaryContainer = BaoBuzzColors.AccentOrange.copy(alpha = 0.1f),
        tertiaryContainer = BaoBuzzColors.AccentGreen.copy(alpha = 0.1f)
    )

private val LightColorScheme =
    lightColorScheme(
        primary = BaoBuzzColors.PrimaryBlue,
        secondary = BaoBuzzColors.AccentOrange,
        tertiary = BaoBuzzColors.AccentGreen,
        error = BaoBuzzColors.ErrorRed,
        background = BaoBuzzColors.BackgroundLight,
        surface = BaoBuzzColors.SurfaceLight,
        surfaceVariant = BaoBuzzColors.SurfaceVariantLight,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onTertiary = Color.White,
        onError = Color.White,
        onBackground = BaoBuzzColors.OnSurfaceLight,
        onSurface = BaoBuzzColors.OnSurfaceLight,
        onSurfaceVariant = BaoBuzzColors.OnSurfaceVariantLight,
        primaryContainer = BaoBuzzColors.PrimaryBlueLight.copy(alpha = 0.1f),
        secondaryContainer = BaoBuzzColors.AccentOrange.copy(alpha = 0.1f),
        tertiaryContainer = BaoBuzzColors.AccentGreen.copy(alpha = 0.1f)
    )

@Composable
fun BaoBuzzTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme =
        if (darkTheme) {
            DarkColorScheme
        } else {
            LightColorScheme
        }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
