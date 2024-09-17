package com.msdc.baobuzz.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.msdc.baobuzz.repository.FootballRepository
import kotlinx.coroutines.launch
import com.msdc.baobuzz.interfaces.Result

class CalendarViewModel(
    private val repository: FootballRepository,
    private val leagueInfoProvider: LeagueInfoProvider
) : ViewModel() {
    private val _fixtures = MutableLiveData<Result<List<Fixture>>>()
    val fixtures: LiveData<Result<List<Fixture>>> = _fixtures

    private val _selectedLeagueId = MutableLiveData<Int>()
    val selectedLeagueId: LiveData<Int> = _selectedLeagueId

    init {
        selectLeague(39) // EPL is selected by default
    }

    fun selectLeague(leagueId: Int) {
        _selectedLeagueId.value = leagueId
        fetchFixtures(leagueId)
    }

    private fun fetchFixtures(leagueId: Int) {
        viewModelScope.launch {
            _fixtures.value = Result.Loading
            _fixtures.value = repository.getUpcomingFixtures(leagueId)
        }
    }

    fun getLeagueInfo(): List<LeagueInfo> = leagueInfoProvider.getAllLeagueInfo()

    class Factory(
        private val repository: FootballRepository,
        private val leagueInfoProvider: LeagueInfoProvider
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CalendarViewModel(repository, leagueInfoProvider) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}