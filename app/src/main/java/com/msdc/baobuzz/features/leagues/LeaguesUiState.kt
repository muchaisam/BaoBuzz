package com.msdc.baobuzz.features.leagues

import com.msdc.baobuzz.core.models.Fixture
import com.msdc.baobuzz.core.models.LeagueStanding
import com.msdc.baobuzz.core.models.Transfer

sealed class LeaguesUiState {
    object Loading : LeaguesUiState()

    data class Success(
        val standings: List<LeagueStanding>,
        val fixtures: List<Fixture>,
        val transfers: List<Transfer>
    ) : LeaguesUiState()

    data class Error(val message: String) : LeaguesUiState()
}
