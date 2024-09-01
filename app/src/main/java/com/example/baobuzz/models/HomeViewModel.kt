package com.example.baobuzz.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HomeViewModel(private val leagueInfoProvider: LeagueInfoProvider) : ViewModel() {

    private val _leagues = MutableLiveData<List<LeagueInfo>>()
    val leagues: LiveData<List<LeagueInfo>> = _leagues

    init {
        loadLeagues()
    }

    private fun loadLeagues() {
        _leagues.value = leagueInfoProvider.getAllLeagueInfo()
    }
}

class HomeViewModelFactory(private val leagueInfoProvider: LeagueInfoProvider) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(leagueInfoProvider) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}