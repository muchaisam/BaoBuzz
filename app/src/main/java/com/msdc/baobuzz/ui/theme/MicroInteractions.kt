package com.msdc.baobuzz.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * MicroInteractions - STUNNING Visual Effects! ✨
 *
 * Advanced animation and interaction effects for BaoBuzz:
 * - Shimmer effects on loading states
 * - Gradient shift animations
 * - Pulsing glow effects
 * - Wave animations
 * - Particle effects
 * - Ripple animations
 */

/**
 * Shimmer Effect - GORGEOUS loading animation! ✨
 *
 * Creates a shimmering light effect that sweeps across content.
 * Perfect for loading states and skeleton screens.
 *
 * @param shimmerColors List of colors for the shimmer gradient
 * @param durationMillis Duration of one shimmer cycle
 * @param delay Delay before animation starts
 */
fun Modifier.shimmerEffect(
    shimmerColors: List<Color> = listOf(
        Color.White.copy(alpha = 0.0f),
        Color.White.copy(alpha = 0.3f),
        Color.White.copy(alpha = 0.5f),
        Color.White.copy(alpha = 0.3f),
        Color.White.copy(alpha = 0.0f)
    ),
    durationMillis: Int = 1500,
    delay: Int = 0
): Modifier = composed {
    var size by remember { mutableStateOf(0f) }

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                delayMillis = delay,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(size * translateAnim - size, size * translateAnim - size),
            end = Offset(size * translateAnim, size * translateAnim)
        )
    )
        .drawWithContent {
            size = this.size.width
            drawContent()
        }
}

/**
 * Gradient Shift Animation - MESMERIZING color flow! 🌈
 *
 * Animates gradient colors with smooth transitions.
 * Creates a living, breathing effect on backgrounds.
 *
 * @param colors List of colors to cycle through
 * @param durationMillis Duration for full color cycle
 */
fun Modifier.animatedGradient(
    colors: List<Color>,
    durationMillis: Int = 3000
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "gradient")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_offset"
    )

    val animatedColors = colors.mapIndexed { index, color ->
        val nextColor = colors.getOrElse(index + 1) { colors.first() }
        lerp(color, nextColor, offset)
    }

    background(
        Brush.linearGradient(
            colors = animatedColors
        )
    )
}

/**
 * Pulsing Glow Effect - RADIANT attention grabber! 💫
 *
 * Creates a pulsing radial glow effect.
 * Perfect for highlighting important elements.
 *
 * @param glowColor Color of the glow
 * @param maxAlpha Maximum alpha for the glow
 * @param durationMillis Duration of one pulse
 */
fun Modifier.pulsingGlow(
    glowColor: Color,
    maxAlpha: Float = 0.5f,
    durationMillis: Int = 2000
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "pulse")
    val alpha by transition.animateFloat(
        initialValue = 0f,
        targetValue = maxAlpha,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = EaseInOut
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    drawWithContent {
        drawContent()
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    glowColor.copy(alpha = alpha),
                    Color.Transparent
                ),
                center = center,
                radius = size.minDimension / 2
            )
        )
    }
}

/**
 * Wave Animation - FLOWING motion effect! 🌊
 *
 * Creates a wave animation across the element.
 * Great for progress indicators and dynamic backgrounds.
 *
 * @param waveColor Color of the wave
 * @param amplitude Height of the wave
 * @param frequency Wave frequency
 * @param speed Animation speed
 */
fun Modifier.waveAnimation(
    waveColor: Color,
    amplitude: Float = 10f,
    frequency: Float = 2f,
    speed: Int = 1000
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "wave")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = speed,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_offset"
    )

    drawWithContent {
        drawContent()

        val waveHeight = amplitude
        val waveFrequency = frequency

        for (x in 0 until size.width.toInt() step 5) {
            val y = (waveHeight * sin((x / size.width * waveFrequency * 2 * PI + offset).toDouble())).toFloat()
            drawCircle(
                color = waveColor,
                radius = 2f,
                center = Offset(x.toFloat(), size.height / 2 + y)
            )
        }
    }
}

/**
 * Breathing Effect - SUBTLE life animation! 🫁
 *
 * Creates a gentle breathing scale effect.
 * Makes UI elements feel alive and responsive.
 *
 * @param minScale Minimum scale
 * @param maxScale Maximum scale
 * @param durationMillis Duration of one breath cycle
 */
@Composable
fun rememberBreathingAnimation(
    minScale: Float = 0.98f,
    maxScale: Float = 1.02f,
    durationMillis: Int = 2000
): Float {
    val transition = rememberInfiniteTransition(label = "breathing")
    return transition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = EaseInOut
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_scale"
    ).value
}

/**
 * Floating Animation - GENTLE levitation effect! 🎈
 *
 * Creates a floating up/down motion.
 * Perfect for badges, icons, and CTAs.
 *
 * @param distance Distance to float (in dp)
 * @param durationMillis Duration of float cycle
 */
@Composable
fun rememberFloatingAnimation(
    distance: Float = 10f,
    durationMillis: Int = 2500
): Float {
    val transition = rememberInfiniteTransition(label = "floating")
    return transition.animateFloat(
        initialValue = 0f,
        targetValue = distance,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = EaseInOutSine
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating_offset"
    ).value
}

/**
 * Rotation Animation - SPINNING effect! 🔄
 *
 * Creates a continuous rotation animation.
 * Great for loading indicators and dynamic icons.
 *
 * @param durationMillis Duration of one full rotation
 */
@Composable
fun rememberRotationAnimation(
    durationMillis: Int = 1000
): Float {
    val transition = rememberInfiniteTransition(label = "rotation")
    return transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation_degrees"
    ).value
}

/**
 * Rainbow Gradient - VIBRANT color cycle! 🌈
 *
 * Creates a cycling rainbow gradient effect.
 * Perfect for premium features and celebrations.
 */
@Composable
fun rememberRainbowGradient(
    durationMillis: Int = 5000
): Brush {
    val transition = rememberInfiniteTransition(label = "rainbow")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rainbow_offset"
    )

    val colors = listOf(
        Color(0xFFFF0080), // Pink
        Color(0xFFFF0000), // Red
        Color(0xFFFF7F00), // Orange
        Color(0xFFFFFF00), // Yellow
        Color(0xFF00FF00), // Green
        Color(0xFF0000FF), // Blue
        Color(0xFF4B0082), // Indigo
        Color(0xFF9400D3)  // Violet
    )

    return Brush.linearGradient(
        colors = colors,
        start = Offset(offset * 1000, 0f),
        end = Offset(offset * 1000 + 500, 500f),
        tileMode = TileMode.Mirror
    )
}

/**
 * Sparkle Effect - GLITTERY magic! ✨
 *
 * Creates random sparkle animations.
 * Perfect for success states and celebrations.
 */
@Composable
fun rememberSparkleAnimation(
    count: Int = 5,
    durationMillis: Int = 1000
): List<Pair<Offset, Float>> {
    val sparkles = remember {
        List(count) {
            val x = (0..100).random() / 100f
            val y = (0..100).random() / 100f
            val delay = (0..500).random()
            Triple(Offset(x, y), delay, (200..400).random())
        }
    }

    return sparkles.map { (offset, delay, duration) ->
        val transition = rememberInfiniteTransition(label = "sparkle_$offset")
        val alpha by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = duration,
                    delayMillis = delay,
                    easing = EaseInOut
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "sparkle_alpha"
        )
        offset to alpha
    }
}

/**
 * Color helper - Linear interpolation between colors
 */
private fun lerp(start: Color, stop: Color, fraction: Float): Color {
    return Color(
        red = start.red + (stop.red - start.red) * fraction,
        green = start.green + (stop.green - start.green) * fraction,
        blue = start.blue + (stop.blue - start.blue) * fraction,
        alpha = start.alpha + (stop.alpha - start.alpha) * fraction
    )
}

/**
 * Easing function for smooth sine wave
 */
private val EaseInOutSine: Easing = Easing { fraction ->
    (-(cos(PI * fraction) - 1) / 2).toFloat()
}
