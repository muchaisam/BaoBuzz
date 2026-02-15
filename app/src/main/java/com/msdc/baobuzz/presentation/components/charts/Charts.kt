package com.msdc.baobuzz.presentation.components.charts

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class ChartData(val label: String, val value: Float, val color: Color)

data class LineChartPoint(val x: Float, val y: Float, val label: String = "")

@Composable
fun DonutChart(
    data: List<ChartData>,
    centerText: String = "",
    centerSubtext: String = "",
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 32.dp,
    animationDuration: Int = 1000
) {
    val total = data.sumOf { it.value.toDouble() }.toFloat()
    val sweepAngles = data.map { (it.value / total) * 360f }

    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by
    animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec =
            tween(durationMillis = animationDuration, easing = EaseInOutCubic),
        label = "donut_chart_animation"
    )

    LaunchedEffect(Unit) { animationPlayed = true }

    Box(modifier = modifier.size(200.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasSize = size.minDimension
            val radius = (canvasSize - strokeWidth.toPx()) / 2
            val center = Offset(size.width / 2, size.height / 2)

            var startAngle = -90f

            data.forEachIndexed { index, chartData ->
                val currentSweepAngle = sweepAngles[index] * animatedProgress

                drawArc(
                    color = chartData.color,
                    startAngle = startAngle,
                    sweepAngle = currentSweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                )

                startAngle += sweepAngles[index]
            }
        }

        // Center Text
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = centerText,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (centerSubtext.isNotEmpty()) {
                Text(
                    text = centerSubtext,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun BarChart(
    data: List<ChartData>,
    modifier: Modifier = Modifier,
    maxBarHeight: Dp = 200.dp,
    barWidth: Dp = 32.dp,
    animationDuration: Int = 800
) {
    val maxValue = data.maxOfOrNull { it.value } ?: 1f

    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by
    animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec =
            tween(durationMillis = animationDuration, easing = EaseInOutCubic),
        label = "bar_chart_animation"
    )

    LaunchedEffect(Unit) { animationPlayed = true }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .height(maxBarHeight)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEach { chartData ->
                val barHeight = (chartData.value / maxValue) * maxBarHeight.value * animatedProgress

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    // Value Label
                    Text(
                        text = chartData.value.toInt().toString(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = chartData.color
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Bar
                    Box(
                        modifier =
                            Modifier
                                .width(barWidth)
                                .height(barHeight.dp)
                                .background(
                                    brush =
                                        Brush.verticalGradient(
                                            colors =
                                                listOf(
                                                    chartData.color,
                                                    chartData.color
                                                        .copy(
                                                            alpha =
                                                                0.7f
                                                        )
                                                )
                                        ),
                                    shape =
                                        RoundedCornerShape(
                                            topStart = 8.dp,
                                            topEnd = 8.dp
                                        )
                                )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Label
                    Text(
                        text = chartData.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun LineChart(
    points: List<LineChartPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    lineWidth: Dp = 3.dp,
    showPoints: Boolean = true,
    showGrid: Boolean = true,
    animationDuration: Int = 1000
) {
    if (points.isEmpty()) return

    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by
    animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec =
            tween(durationMillis = animationDuration, easing = EaseInOutCubic),
        label = "line_chart_animation"
    )

    LaunchedEffect(Unit) { animationPlayed = true }

    val minX = points.minOfOrNull { it.x } ?: 0f
    val maxX = points.maxOfOrNull { it.x } ?: 1f
    val minY = points.minOfOrNull { it.y } ?: 0f
    val maxY = points.maxOfOrNull { it.y } ?: 1f

    Box(modifier = modifier
        .fillMaxWidth()
        .height(200.dp)
        .padding(16.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Draw grid
            if (showGrid) {
                val gridColor = Color.Gray.copy(alpha = 0.3f)

                // Horizontal grid lines
                for (i in 0..4) {
                    val y = height * i / 4
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Vertical grid lines
                for (i in 0..4) {
                    val x = width * i / 4
                    drawLine(
                        color = gridColor,
                        start = Offset(x, 0f),
                        end = Offset(x, height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }

            // Convert points to canvas coordinates
            val canvasPoints =
                points.map { point ->
                    Offset(
                        x = ((point.x - minX) / (maxX - minX)) * width,
                        y = height - ((point.y - minY) / (maxY - minY)) * height
                    )
                }

            // Draw animated line
            if (canvasPoints.size > 1) {
                val path =
                    Path().apply {
                        moveTo(canvasPoints.first().x, canvasPoints.first().y)

                        val animatedPointCount = (canvasPoints.size * animatedProgress).toInt()
                        val animatedPoints = canvasPoints.take(maxOf(1, animatedPointCount))

                        for (i in 1 until animatedPoints.size) {
                            lineTo(animatedPoints[i].x, animatedPoints[i].y)
                        }

                        // Add partial line for smooth animation
                        if (animatedProgress < 1f && animatedPointCount < canvasPoints.size - 1
                        ) {
                            val nextPoint = canvasPoints[animatedPointCount]
                            val currentPoint = canvasPoints[animatedPointCount - 1]
                            val partialProgress =
                                (canvasPoints.size * animatedProgress) - animatedPointCount

                            val partialX =
                                currentPoint.x +
                                        (nextPoint.x - currentPoint.x) * partialProgress
                            val partialY =
                                currentPoint.y +
                                        (nextPoint.y - currentPoint.y) * partialProgress

                            lineTo(partialX, partialY)
                        }
                    }

                drawPath(
                    path = path,
                    color = lineColor,
                    style =
                        Stroke(
                            width = lineWidth.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                )
            }

            // Draw points
            if (showPoints) {
                val animatedPointCount = (canvasPoints.size * animatedProgress).toInt()
                canvasPoints.take(animatedPointCount).forEach { point ->
                    drawCircle(color = lineColor, radius = 6.dp.toPx(), center = point)

                    drawCircle(color = Color.White, radius = 3.dp.toPx(), center = point)
                }
            }
        }
    }
}

@Composable
fun ProgressCircle(
    progress: Float,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 12.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    circleSize: Dp = 100.dp,
    animationDuration: Int = 1000
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by
    animateFloatAsState(
        targetValue = if (animationPlayed) progress else 0f,
        animationSpec =
            tween(durationMillis = animationDuration, easing = EaseInOutCubic),
        label = "progress_circle_animation"
    )

    LaunchedEffect(Unit) { animationPlayed = true }

    Box(modifier = modifier.size(circleSize), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasSize = size.minDimension
            val radius = (canvasSize - strokeWidth.toPx()) / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Background circle
            drawArc(
                color = backgroundColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            // Progress arc
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }

        Text(
            text = "${(animatedProgress * 100).toInt()}%",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun HorizontalBarChart(
    data: List<ChartData>,
    modifier: Modifier = Modifier,
    maxBarWidth: Dp = 200.dp,
    barHeight: Dp = 24.dp,
    animationDuration: Int = 800
) {
    val maxValue = data.maxOfOrNull { it.value } ?: 1f

    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by
    animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec =
            tween(durationMillis = animationDuration, easing = EaseInOutCubic),
        label = "horizontal_bar_chart_animation"
    )

    LaunchedEffect(Unit) { animationPlayed = true }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        data.forEach { chartData ->
            val barWidth = (chartData.value / maxValue) * maxBarWidth.value * animatedProgress

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Label
                Text(
                    text = chartData.label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(80.dp)
                )

                // Bar container
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(barHeight)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(barHeight / 2)
                            )
                ) {
                    // Animated bar
                    Box(
                        modifier =
                            Modifier
                                .width(barWidth.dp)
                                .fillMaxHeight()
                                .background(
                                    brush =
                                        Brush.horizontalGradient(
                                            colors =
                                                listOf(
                                                    chartData.color,
                                                    chartData.color
                                                        .copy(
                                                            alpha =
                                                                0.8f
                                                        )
                                                )
                                        ),
                                    shape = RoundedCornerShape(barHeight / 2)
                                )
                    )
                }

                // Value
                Text(
                    text = chartData.value.toInt().toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = chartData.color,
                    modifier = Modifier.width(40.dp)
                )
            }
        }
    }
}

@Composable
fun RadarChart(
    data: List<ChartData>,
    modifier: Modifier = Modifier,
    maxValue: Float = 100f,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    fillColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
    animationDuration: Int = 1000
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by
    animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec =
            tween(durationMillis = animationDuration, easing = EaseInOutCubic),
        label = "radar_chart_animation"
    )

    LaunchedEffect(Unit) { animationPlayed = true }

    Box(modifier = modifier
        .size(200.dp)
        .padding(32.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2
            val angleStep = 2 * PI / data.size

            // Draw background grid
            val gridColor = Color.Gray.copy(alpha = 0.3f)
            for (i in 1..5) {
                val gridRadius = radius * i / 5
                drawCircle(
                    color = gridColor,
                    radius = gridRadius,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Draw axis lines
            data.forEachIndexed { index, _ ->
                val angle = -PI / 2 + index * angleStep
                val endPoint =
                    Offset(
                        center.x + cos(angle).toFloat() * radius,
                        center.y + sin(angle).toFloat() * radius
                    )

                drawLine(
                    color = gridColor,
                    start = center,
                    end = endPoint,
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Calculate data points
            val dataPoints =
                data.mapIndexed { index, chartData ->
                    val angle = -PI / 2 + index * angleStep
                    val distance = (chartData.value / maxValue) * radius * animatedProgress

                    Offset(
                        center.x + cos(angle).toFloat() * distance,
                        center.y + sin(angle).toFloat() * distance
                    )
                }

            // Draw filled area
            if (dataPoints.size > 2 && animatedProgress > 0) {
                val path =
                    Path().apply {
                        moveTo(dataPoints.first().x, dataPoints.first().y)
                        for (i in 1 until dataPoints.size) {
                            lineTo(dataPoints[i].x, dataPoints[i].y)
                        }
                        close()
                    }

                drawPath(path = path, color = fillColor)

                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 2.dp.toPx(), join = StrokeJoin.Round)
                )
            }

            // Draw data points
            dataPoints.forEach { point ->
                drawCircle(color = lineColor, radius = 4.dp.toPx(), center = point)

                drawCircle(color = Color.White, radius = 2.dp.toPx(), center = point)
            }
        }

        // Labels are rendered inside Canvas for simplicity
        // TODO: Re-implement with proper density scope if needed
    }
}
