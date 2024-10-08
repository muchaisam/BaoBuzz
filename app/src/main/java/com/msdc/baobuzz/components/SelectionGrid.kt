package com.msdc.baobuzz.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.msdc.baobuzz.models.Team
import com.msdc.baobuzz.viewmodel.LeagueSelectionViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> SelectionGrid(
    items: List<T>,
    selectedIds: Set<Int>,
    onItemClick: (T) -> Unit,
    itemContent: @Composable (T) -> Unit,
    loadingContent: @Composable () -> Unit,
    emptyContent: @Composable () -> Unit,
    uiState: LeagueSelectionViewModel.UiState,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is LeagueSelectionViewModel.UiState.Loading -> loadingContent()
        is LeagueSelectionViewModel.UiState.Success -> {
            if (items.isEmpty()) {
                emptyContent()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = modifier
                ) {
                    items(items.size) { index ->
                        val item = items[index]
                        Box(
                            modifier = Modifier
                                .animateItemPlacement()
                                .clickable { onItemClick(item) }
                        ) {
                            itemContent(item)
                        }
                    }
                }
            }
        }

        is LeagueSelectionViewModel.UiState.Error -> {
            ErrorMessage(message = uiState.message)
        }
    }
}

@Composable
fun SelectionItem(
    text: String,
    imageUrl: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        label = "selection scale"
    )

    Column(
        modifier = modifier
            .scale(scale)
            .aspectRatio(1f)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant,
                CircleShape
            )
            .border(
                width = 2.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else Color.Transparent,
                shape = CircleShape
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Fit
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
        )
    }
}


@Composable
fun NavigationButtons(
    currentStep: Int,
    totalSteps: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (currentStep > 0) {
            Button(
                onClick = onPrevious,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Previous")
            }
        }
        Button(
            onClick = onNext,
            modifier = Modifier.weight(1f)
        ) {
            Text(if (currentStep == totalSteps - 1) "Finish" else "Next")
        }
    }
}
