package com.msdc.baobuzz.features.comparison

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msdc.baobuzz.core.models.SeasonComparison
import com.msdc.baobuzz.core.models.SeasonStats
import com.msdc.baobuzz.presentation.components.LoadingStateCard

/**
 * ComparisonScreen - Season Comparison with STUNNING Visual Charts! 🔄📊
 *
 * Features:
 * - League selector dropdown
 * - Season selectors (Season 1 vs Season 2)
 * - Side-by-side statistics comparison
 * - Animated bar charts
 * - Performance indicators
 * - Glassmorphic design throughout
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonScreen(
    onNavigateBack: () -> Unit,
    viewModel: ComparisonViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var selectedLeague by remember { mutableStateOf("en.1") }
    var selectedSeason1 by remember { mutableStateOf<String?>(null) }
    var selectedSeason2 by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            ComparisonTopBar(onNavigateBack = onNavigateBack)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is ComparisonUiState.Initial -> {
                    ComparisonInitialScreen(
                        availableLeagues = viewModel.getAvailableLeagues(),
                        availableSeasons = viewModel.getAvailableSeasons(),
                        selectedLeague = selectedLeague,
                        selectedSeason1 = selectedSeason1,
                        selectedSeason2 = selectedSeason2,
                        onLeagueSelected = { selectedLeague = it },
                        onSeason1Selected = { selectedSeason1 = it },
                        onSeason2Selected = { selectedSeason2 = it },
                        onCompare = {
                            if (selectedSeason1 != null && selectedSeason2 != null) {
                                viewModel.compareSeasons(
                                    selectedLeague,
                                    selectedSeason1!!,
                                    selectedSeason2!!
                                )
                            }
                        }
                    )
                }

                is ComparisonUiState.Loading -> {
                    LoadingStateCard(message = "Comparing seasons...")
                }

                is ComparisonUiState.Success -> {
                    ComparisonSuccessScreen(
                        comparison = state.comparison,
                        onReset = { viewModel.reset() }
                    )
                }

                is ComparisonUiState.Error -> {
                    ErrorStateCard(
                        message = state.message,
                        onRetry = { viewModel.reset() }
                    )
                }
            }
        }
    }
}

/**
 * Top Bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComparisonTopBar(onNavigateBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🔄", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Season Comparison",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier.background(
            Brush.horizontalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                )
            )
        )
    )
}

/**
 * Initial screen with selectors
 */
@Composable
private fun ComparisonInitialScreen(
    availableLeagues: Map<String, String>,
    availableSeasons: List<String>,
    selectedLeague: String,
    selectedSeason1: String?,
    selectedSeason2: String?,
    onLeagueSelected: (String) -> Unit,
    onSeason1Selected: (String) -> Unit,
    onSeason2Selected: (String) -> Unit,
    onCompare: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🔄", style = MaterialTheme.typography.displayMedium)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Compare Seasons",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Select two seasons to compare side-by-side",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // League Selector
        SelectorCard(
            label = "League",
            selectedValue = availableLeagues[selectedLeague] ?: "Premier League",
            icon = "🏆"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Season Selectors Side by Side
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                SeasonSelectorCard(
                    label = "Season 1",
                    selectedSeason = selectedSeason1,
                    availableSeasons = availableSeasons,
                    onSeasonSelected = onSeason1Selected,
                    icon = "📅"
                )
            }

            // VS Badge
            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(48.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                            )
                        ),
                        CircleShape
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "VS",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                SeasonSelectorCard(
                    label = "Season 2",
                    selectedSeason = selectedSeason2,
                    availableSeasons = availableSeasons,
                    onSeasonSelected = onSeason2Selected,
                    icon = "📅"
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Compare Button
        Button(
            onClick = onCompare,
            enabled = selectedSeason1 != null && selectedSeason2 != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Compare Seasons",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Selector Card
 */
@Composable
private fun SelectorCard(
    label: String,
    selectedValue: String,
    icon: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = icon, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = selectedValue,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Season Selector Card
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeasonSelectorCard(
    label: String,
    selectedSeason: String?,
    availableSeasons: List<String>,
    onSeasonSelected: (String) -> Unit,
    icon: String
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { expanded = true },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    color = if (selectedSeason != null)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    else
                        Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = selectedSeason ?: "Select",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedSeason != null)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Dropdown menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableSeasons.forEach { season ->
                DropdownMenuItem(
                    text = { Text(season) },
                    onClick = {
                        onSeasonSelected(season)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Success screen with comparison results
 */
@Composable
private fun ComparisonSuccessScreen(
    comparison: SeasonComparison,
    onReset: () -> Unit
) {
    // Entrance animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically { it }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with league name
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = comparison.league,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "${comparison.season1.season} vs ${comparison.season2.season}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onReset) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "New comparison",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Champions comparison
            item {
                ChampionsComparisonCard(
                    season1 = comparison.season1,
                    season2 = comparison.season2
                )
            }

            // Points comparison with animated bar chart
            item {
                StatComparisonCard(
                    label = "Champion Points",
                    value1 = comparison.season1.championPoints,
                    value2 = comparison.season2.championPoints,
                    icon = "🏆",
                    season1Label = comparison.season1.season,
                    season2Label = comparison.season2.season
                )
            }

            // Goals comparison
            item {
                StatComparisonCard(
                    label = "Total Goals",
                    value1 = comparison.season1.totalGoals,
                    value2 = comparison.season2.totalGoals,
                    icon = "⚽",
                    season1Label = comparison.season1.season,
                    season2Label = comparison.season2.season
                )
            }

            // Top scorers comparison
            item {
                TopScorersComparisonCard(
                    season1 = comparison.season1,
                    season2 = comparison.season2
                )
            }

            // Average goals per match
            item {
                FloatStatComparisonCard(
                    label = "Avg Goals/Match",
                    value1 = comparison.season1.averageGoalsPerMatch,
                    value2 = comparison.season2.averageGoalsPerMatch,
                    icon = "📊",
                    season1Label = comparison.season1.season,
                    season2Label = comparison.season2.season
                )
            }

            // Analysis summary
            item {
                AnalysisSummaryCard(comparison = comparison)
            }
        }
    }
}

/**
 * Champions Comparison Card
 */
@Composable
private fun ChampionsComparisonCard(
    season1: SeasonStats,
    season2: SeasonStats
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFD700).copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFD700).copy(alpha = 0.3f),
                            Color(0xFFFFA726).copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "👑", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Champions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = season1.season,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = season1.champion,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "${season1.championPoints} pts",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFFFD700)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(80.dp)
                            .background(Color.White.copy(alpha = 0.2f))
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = season2.season,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = season2.champion,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "${season2.championPoints} pts",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFFFD700)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Stat Comparison Card with animated bar chart
 */
@Composable
private fun StatComparisonCard(
    label: String,
    value1: Int,
    value2: Int,
    icon: String,
    season1Label: String,
    season2Label: String
) {
    val maxValue = maxOf(value1, value2).toFloat()
    val progress1 by animateFloatAsState(
        targetValue = value1 / maxValue,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "progress1"
    )
    val progress2 by animateFloatAsState(
        targetValue = value2 / maxValue,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "progress2"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = icon, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Season 1 bar
                ComparisonBar(
                    label = season1Label,
                    value = value1,
                    progress = progress1,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Season 2 bar
                ComparisonBar(
                    label = season2Label,
                    value = value2,
                    progress = progress2,
                    color = MaterialTheme.colorScheme.secondary
                )

                // Winner indicator
                if (value1 != value2) {
                    Spacer(modifier = Modifier.height(12.dp))
                    val winner = if (value1 > value2) season1Label else season2Label
                    val difference = kotlin.math.abs(value1 - value2)
                    Text(
                        text = "🏆 $winner leads by $difference",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (value1 > value2)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Comparison Bar
 */
@Composable
private fun ComparisonBar(
    label: String,
    value: Int,
    progress: Float,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(color.copy(alpha = 0.15f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(color, color.copy(alpha = 0.7f))
                        ),
                        RoundedCornerShape(6.dp)
                    )
            )
        }
    }
}

// Continue in next part...
