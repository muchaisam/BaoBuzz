package com.example.baobuzz.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.baobuzz.models.CareerStep
import com.example.baobuzz.models.Coach
import com.example.baobuzz.models.CoachUiState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.baobuzz.R
import com.example.baobuzz.models.CoachViewModel

@Composable
fun CoachCareerViewer(
    modifier: Modifier = Modifier,
    viewModel: CoachViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedCoach by viewModel.selectedCoach.collectAsState()

    when (val state = uiState) {
        is CoachUiState.Loading -> LoadingScreen(cardCount = 3)
        is CoachUiState.Error -> ErrorScreen(state.message)
        is CoachUiState.Success -> {
            Box(modifier = modifier.fillMaxSize()) {
                CoachList(
                    coaches = state.coaches,
                    onCoachSelected = { viewModel.selectCoach(it) }
                )

                selectedCoach?.let { coach ->
                    CoachDetailsDialog(
                        coach = coach,
                        onDismiss = { viewModel.clearSelectedCoach() }
                    )
                }
            }
        }
    }
}


@Composable
fun CoachList(
    coaches: List<Coach>,
    onCoachSelected: (Coach) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(coaches) { coach ->
            CoachCard(coach = coach, onClick = { onCoachSelected(coach) })
        }
    }
}

@Composable
fun CoachCard(
    coach: Coach,
    onClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .width(200.dp)
            .clickable { showDialog = true },
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = coach.photo,
                contentDescription = "Coach ${coach.name}",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = coach.name, style = MaterialTheme.typography.h6.copy(fontFamily = AppFontFamily.Gabarito))
            Text(text = coach.team.name, style = MaterialTheme.typography.body2.copy(fontFamily = AppFontFamily.Gabarito))
            Text(text = "Age: ${coach.age}", style = MaterialTheme.typography.body2.copy(fontFamily = AppFontFamily.Gabarito))
        }
    }

    if (showDialog) {
        CoachDetailsDialog(
            coach = coach,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun CoachDetailsDialog(
    coach: Coach,
    onDismiss: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(if (isVisible) 1f else 0f)
    val slideOffset by animateFloatAsState(if (isVisible) 0f else 1f)

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Dialog(
        onDismissRequest = {
            isVisible = false
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    alpha = alpha,
                    translationY = slideOffset * 300f
                )
        ) {
            Card(
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
                    .align(Alignment.BottomCenter)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Managerial History",
                        style = MaterialTheme.typography.h5.copy(fontFamily = AppFontFamily.Gabarito)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CareerTimeline(coach.career)
                }
            }
        }
    }
}


@Composable
fun LoadingScreen(cardCount: Int = 3) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ShimmerLoadingEffect(cardCount)
    }
}

@Composable
fun ShimmerLoadingEffect(
    cardCount: Int,
    colors: List<Color> = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )
) {
    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        )
    )

    val brush = Brush.linearGradient(
        colors = colors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(cardCount) {
            ShimmerCard(brush)
        }
    }
}

@Composable
fun ShimmerCard(brush: Brush) {
    Column(
        modifier = Modifier
            .width(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.surface)
            .padding(16.dp)
    ) {
        // Shimmer effect for profile picture
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(50))
                .background(brush)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Shimmer effect for name
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Shimmer effect for team name
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Shimmer effect for age
        Box(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun ErrorScreen(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Error: $message", color = MaterialTheme.colors.error)
    }
}

@Composable
fun CareerTimeline(career: List<CareerStep>) {
    LazyColumn {
        itemsIndexed(career.reversed()) { index, step ->
            Row {
                TimelineConnector(
                    isFirst = index == 0,
                    isLast = index == career.size - 1
                )
                CareerStepItem(step)
            }
        }
    }
}

@Composable
fun TimelineConnector(isFirst: Boolean, isLast: Boolean) {
    Box(
        modifier = Modifier
            .width(24.dp)
            .height(80.dp)
    ) {
        if (!isFirst) {
            Divider(
                color = MaterialTheme.colors.primary,
                modifier = Modifier
                    .width(2.dp)
                    .height(40.dp)
                    .align(Alignment.TopCenter)
            )
        }
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(MaterialTheme.colors.primary, CircleShape)
                .align(Alignment.Center)
        )
        if (!isLast) {
            Divider(
                color = MaterialTheme.colors.primary,
                modifier = Modifier
                    .width(2.dp)
                    .height(40.dp)
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun CareerStepItem(step: CareerStep) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { expanded = !expanded }
            .animateContentSize(),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = step.team.name,
                    style = MaterialTheme.typography.h6.copy(fontFamily = AppFontFamily.Gabarito)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier.rotate(if (expanded) 180f else 0f)
                )
            }
            Text(
                text = "${step.start} - ${step.end ?: "Present"}",
                style = MaterialTheme.typography.body2.copy(fontFamily = AppFontFamily.Gabarito)
            )
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Additional details about this role...",
                    style = MaterialTheme.typography.body2.copy(fontFamily = AppFontFamily.Gabarito)
                )
            }
        }
    }
}

object AppFontFamily {
    val Gabarito = FontFamily(
        Font(R.font.ga_regular),
        Font(R.font.ga_regular, weight = FontWeight.Bold)
    )
}

