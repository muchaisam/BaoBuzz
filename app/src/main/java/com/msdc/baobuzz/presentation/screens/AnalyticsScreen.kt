package com.msdc.baobuzz.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.msdc.baobuzz.presentation.components.charts.BarChart
import com.msdc.baobuzz.presentation.components.charts.ChartData
import com.msdc.baobuzz.presentation.components.charts.DonutChart
import com.msdc.baobuzz.presentation.components.charts.HorizontalBarChart
import com.msdc.baobuzz.presentation.components.charts.LineChart
import com.msdc.baobuzz.presentation.components.charts.LineChartPoint
import com.msdc.baobuzz.presentation.components.charts.ProgressCircle
import com.msdc.baobuzz.presentation.components.charts.RadarChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(onNavigateBack: () -> Unit = {}, modifier: Modifier = Modifier) {
    var selectedPeriod by remember { mutableStateOf("This Season") }
    val periods = listOf("This Week", "This Month", "This Season", "All Time")

    Column(modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        // Header
        AnalyticsHeader(
            onNavigateBack = onNavigateBack,
            selectedPeriod = selectedPeriod,
            periods = periods,
            onPeriodChange = { selectedPeriod = it }
        )

        // Content
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { PerformanceOverviewCard() }

            item { GoalStatisticsCard() }

            item { TeamComparisonCard() }

            item { PlayerPerformanceCard() }

            item { SeasonProgressCard() }

            item { MatchResultsDistributionCard() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnalyticsHeader(
    onNavigateBack: () -> Unit,
    selectedPeriod: String,
    periods: List<String>,
    onPeriodChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column {
                        Text(
                            text = "Analytics",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Performance insights and statistics",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Outlined.Analytics,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Period Selection
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // ✅ ADDED KEY - Performance optimization
                items(
                    items = periods,
                    key = { it }
                ) { period ->
                    FilterChip(
                        selected = selectedPeriod == period,
                        onClick = { onPeriodChange(period) },
                        label = {
                            Text(text = period, style = MaterialTheme.typography.labelLarge)
                        },
                        colors =
                            FilterChipDefaults.filterChipColors(
                                selectedContainerColor =
                                    MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun PerformanceOverviewCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Performance Overview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    imageVector = Icons.Outlined.TrendingUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Performance metrics with progress circles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PerformanceMetric(
                    title = "Win Rate",
                    value = 68,
                    maxValue = 100,
                    color = Color(0xFF4CAF50)
                )

                PerformanceMetric(
                    title = "Goals/Game",
                    value = 85,
                    maxValue = 100,
                    color = MaterialTheme.colorScheme.primary
                )

                PerformanceMetric(
                    title = "Defense",
                    value = 72,
                    maxValue = 100,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
private fun PerformanceMetric(
    title: String,
    value: Int,
    maxValue: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        ProgressCircle(
            progress = value.toFloat() / maxValue.toFloat(),
            color = color,
            circleSize = 80.dp,
            strokeWidth = 8.dp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun GoalStatisticsCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Goals This Season",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            val goalData =
                listOf(
                    LineChartPoint(1f, 2f, "Aug"),
                    LineChartPoint(2f, 5f, "Sep"),
                    LineChartPoint(3f, 3f, "Oct"),
                    LineChartPoint(4f, 8f, "Nov"),
                    LineChartPoint(5f, 6f, "Dec"),
                    LineChartPoint(6f, 10f, "Jan")
                )

            LineChart(
                points = goalData,
                lineColor = MaterialTheme.colorScheme.primary,
                showGrid = true
            )
        }
    }
}

@Composable
private fun TeamComparisonCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "League Comparison",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            val comparisonData =
                listOf(
                    ChartData("Goals", 78f, Color(0xFF4CAF50)),
                    ChartData("Assists", 52f, MaterialTheme.colorScheme.primary),
                    ChartData("Clean Sheets", 15f, MaterialTheme.colorScheme.tertiary),
                    ChartData("Yellow Cards", 45f, Color(0xFFFF9800)),
                    ChartData("Red Cards", 3f, Color(0xFFF44336))
                )

            HorizontalBarChart(data = comparisonData, maxBarWidth = 180.dp)
        }
    }
}

@Composable
private fun PlayerPerformanceCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Player Attributes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            val playerAttributes =
                listOf(
                    ChartData("Speed", 92f, MaterialTheme.colorScheme.primary),
                    ChartData("Shooting", 88f, Color(0xFF4CAF50)),
                    ChartData("Passing", 82f, MaterialTheme.colorScheme.tertiary),
                    ChartData("Dribbling", 90f, Color(0xFFFF9800)),
                    ChartData("Defense", 45f, Color(0xFFF44336)),
                    ChartData("Physical", 78f, Color(0xFF9C27B0))
                )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RadarChart(data = playerAttributes, maxValue = 100f)

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    playerAttributes.forEach { attribute ->
                        PlayerAttributeItem(
                            name = attribute.label,
                            value = attribute.value.toInt(),
                            color = attribute.color
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerAttributeItem(
    name: String,
    value: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(modifier = Modifier
            .size(12.dp)
            .background(color = color, shape = CircleShape))

        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(60.dp)
        )

        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun SeasonProgressCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Season Progress",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            val progressData =
                listOf(
                    ChartData("Jan", 8f, MaterialTheme.colorScheme.primary),
                    ChartData("Feb", 6f, MaterialTheme.colorScheme.primary),
                    ChartData("Mar", 10f, MaterialTheme.colorScheme.primary),
                    ChartData("Apr", 7f, MaterialTheme.colorScheme.primary),
                    ChartData("May", 12f, MaterialTheme.colorScheme.primary),
                    ChartData("Jun", 4f, MaterialTheme.colorScheme.primary)
                )

            BarChart(data = progressData, maxBarHeight = 150.dp, barWidth = 28.dp)
        }
    }
}

@Composable
private fun MatchResultsDistributionCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Match Results Distribution",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val distributionData =
                    listOf(
                        ChartData("Wins", 18f, Color(0xFF4CAF50)),
                        ChartData("Draws", 8f, Color(0xFFFF9800)),
                        ChartData("Losses", 6f, Color(0xFFF44336))
                    )

                DonutChart(data = distributionData, centerText = "32", centerSubtext = "Matches")

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    distributionData.forEach { result ->
                        ResultItem(
                            label = result.label,
                            count = result.value.toInt(),
                            color = result.color,
                            total = distributionData.sumOf { it.value.toDouble() }.toInt()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultItem(
    label: String,
    count: Int,
    color: Color,
    total: Int,
    modifier: Modifier = Modifier
) {
    val percentage = (count.toFloat() / total.toFloat() * 100).toInt()

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier
            .size(16.dp)
            .background(color = color, shape = CircleShape))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = color
                )

                Text(
                    text = "($percentage%)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
