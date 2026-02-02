package com.msdc.baobuzz.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Modern glassmorphism effects for BaoBuzz
 * Creates sleek, frosted glass appearance with transparency and blur
 */

/**
 * Applies a glassmorphism effect to any composable
 *
 * @param backgroundColor Base color with alpha for transparency
 * @param borderColor Optional border color for glass edge effect
 * @param borderWidth Width of the glass border
 * @param blurRadius Blur amount for frosted glass effect
 */
fun Modifier.glassmorphic(
    backgroundColor: Color,
    borderColor: Color? = null,
    borderWidth: Dp = 1.dp,
    blurRadius: Dp = 0.dp
): Modifier {
    var modifier = this.background(backgroundColor)

    if (blurRadius > 0.dp) {
        modifier = modifier.blur(blurRadius)
    }

    if (borderColor != null) {
        modifier = modifier.border(
            width = borderWidth,
            color = borderColor
        )
    }

    return modifier
}

/**
 * Creates a gradient glassmorphism effect
 * Perfect for card backgrounds and surfaces
 */
fun Modifier.glassGradient(
    colors: List<Color>,
    alpha: Float = 0.1f
): Modifier {
    return this.background(
        Brush.verticalGradient(
            colors = colors.map { it.copy(alpha = alpha) }
        )
    )
}

/**
 * Creates a radial glassmorphism effect
 * Great for spotlight or focal point effects
 */
fun Modifier.glassRadial(
    centerColor: Color,
    edgeColor: Color,
    alpha: Float = 0.15f
): Modifier {
    return this.background(
        Brush.radialGradient(
            colors = listOf(
                centerColor.copy(alpha = alpha),
                edgeColor.copy(alpha = alpha * 0.5f)
            )
        )
    )
}

/**
 * Pre-defined glass effects for consistency
 */
object GlassEffects {
    /**
     * Primary glass effect for main cards
     */
    fun Modifier.primaryGlass(isDark: Boolean): Modifier {
        return this.background(
            if (isDark) {
                Color.White.copy(alpha = 0.05f)
            } else {
                Color.White.copy(alpha = 0.7f)
            }
        )
    }

    /**
     * Secondary glass effect for nested cards
     */
    fun Modifier.secondaryGlass(isDark: Boolean): Modifier {
        return this.background(
            if (isDark) {
                Color.White.copy(alpha = 0.03f)
            } else {
                Color.White.copy(alpha = 0.5f)
            }
        )
    }

    /**
     * Accent glass effect for highlighted content
     */
    fun Modifier.accentGlass(accentColor: Color): Modifier {
        return this.background(
            Brush.verticalGradient(
                colors = listOf(
                    accentColor.copy(alpha = 0.15f),
                    accentColor.copy(alpha = 0.05f)
                )
            )
        )
    }
}
