package com.example.baobuzz.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.baobuzz.repository.FootballRepository


class HomeViewModel(
    private val leagueInfoProvider: LeagueInfoProvider
) : ViewModel() {

    private val _leagues = MutableLiveData<List<LeagueInfo>>()
    val leagues: LiveData<List<LeagueInfo>> = _leagues

    init {
        loadLeagues()
    }

    private fun loadLeagues() {
        _leagues.value = leagueInfoProvider.getAllLeagueInfo()
    }
}
