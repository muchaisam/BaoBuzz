package com.msdc.baobuzz.viewmodelfactory

import com.msdc.baobuzz.viewmodel.LeagueSelectionViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.msdc.baobuzz.repository.LeagueRepository
import com.msdc.baobuzz.repository.TeamRepository
import com.msdc.baobuzz.repository.UserPreferencesRepository

class LeagueSelectionViewModelFactory(
    private val leagueRepository: LeagueRepository,
    private val teamRepository: TeamRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeagueSelectionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LeagueSelectionViewModel(leagueRepository, teamRepository, userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}