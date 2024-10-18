package com.msdc.baobuzz.components.browsescreen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable

@Composable
fun ShimmerCompetitionList() {
    LazyColumn {
        items(10) {
            ShimmerCompetitionItem()
        }
    }
}

