package com.msdc.baobuzz.features.stats

import com.msdc.baobuzz.core.models.PlayerStat

sealed class StatsUiState {
    object Loading : StatsUiState()

    data class Success(
        val topScorers: List<PlayerStat>,
        val topAssisters: List<PlayerStat>
    ) : StatsUiState()

    data class Error(val message: String) : StatsUiState()
}
