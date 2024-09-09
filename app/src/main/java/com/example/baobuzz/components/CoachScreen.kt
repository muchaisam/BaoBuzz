package com.example.baobuzz.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.baobuzz.models.CareerStep
import com.example.baobuzz.models.Coach
import com.example.baobuzz.models.CoachUiState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.baobuzz.models.CoachViewModel

@Composable
fun CoachCareerViewer(
    modifier: Modifier = Modifier,
    viewModel: CoachViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedCoaches by viewModel.selectedCoaches.collectAsState()

    when (val state = uiState) {
        is CoachUiState.Loading -> LoadingScreen()
        is CoachUiState.Error -> ErrorScreen(state.message)
        is CoachUiState.Success -> {
            if (selectedCoaches.isEmpty()) {
                SingleCoachView(state.coach, onSelectForComparison = { viewModel.toggleCoachSelection(it) })
            } else {
                CoachComparisonView(selectedCoaches)
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Error: $message", color = MaterialTheme.colors.error)
    }
}


@Composable
fun CoachHeader(coach: Coach) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = coach.photo,
            contentDescription = "Coach ${coach.name}",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
        )
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(text = coach.name, style = MaterialTheme.typography.h5)
            Text(text = "Age: ${coach.age}", style = MaterialTheme.typography.body1)
            Text(text = "Current Team: ${coach.team.name}", style = MaterialTheme.typography.body1)
        }
    }
}


@Composable
fun SingleCoachView(
    coach: Coach,
    onSelectForComparison: (Coach) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        CoachHeader(coach)
        Button(onClick = { onSelectForComparison(coach) }) {
            Text("Select for Comparison")
        }
        CareerTimeline(coach.career)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CoachComparisonView(coaches: List<Coach>) {
    val pagerState = rememberPagerState { coaches.size }

    HorizontalPager(
        state = pagerState
    ) { page ->
        SingleCoachView(coaches[page], onSelectForComparison = {})
    }
}

@Composable
fun CareerTimeline(career: List<CareerStep>) {
    val scale = remember { mutableStateOf(1f) }
    val offset = remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale.value *= zoom
                    offset.value += pan
                }
            }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .height(100.dp)
                .graphicsLayer(
                    scaleX = scale.value,
                    scaleY = scale.value,
                    translationX = offset.value.x,
                    translationY = offset.value.y
                )
        ) {
            items(career) { step ->
                CareerStepItem(step, isLast = step == career.last())
            }
        }
    }
}

@Composable
fun CareerStepItem(step: CareerStep, isLast: Boolean) {
    var expanded by remember { mutableStateOf(false) }

    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { expanded = !expanded }
    ) {
        TimelineNode(isLast)
        Column(modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)) {
            Text(text = step.team.name, style = MaterialTheme.typography.h6)
            Text(text = "From: ${step.start}")
            Text(text = "To: ${step.end ?: "Present"}")
        }
    }
}


@Composable
fun TimelineNode(isLast: Boolean) {
    Box(
        modifier = Modifier
            .width(24.dp)
            .height(if (isLast) 24.dp else 100.dp)
    ) {
        Divider(
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .width(2.dp)
                .height(if (isLast) 0.dp else 100.dp)
                .align(Alignment.TopCenter)
        )
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(MaterialTheme.colors.primary, CircleShape)
                .align(Alignment.TopCenter)
        )
    }
}
