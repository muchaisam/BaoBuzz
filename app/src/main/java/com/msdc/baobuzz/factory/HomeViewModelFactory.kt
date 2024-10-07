package com.msdc.baobuzz.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.msdc.baobuzz.models.LeagueInfoProvider
import com.msdc.baobuzz.viewmodel.HomeViewModel

class HomeViewModelFactory(private val leagueInfoProvider: LeagueInfoProvider) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(leagueInfoProvider) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}