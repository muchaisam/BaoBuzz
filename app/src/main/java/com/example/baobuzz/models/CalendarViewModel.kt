package com.example.baobuzz.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.baobuzz.repository.FootballRepository
import kotlinx.coroutines.launch
import com.example.baobuzz.interfaces.Result

class CalendarViewModel(private val repository: FootballRepository) : ViewModel() {
    private val _leaguesWithFixtures = MutableLiveData<Result<List<LeagueWithFixtures>>>()
    val leaguesWithFixtures: LiveData<Result<List<LeagueWithFixtures>>> = _leaguesWithFixtures

    private val topFiveLeagues = listOf(39, 140, 61, 78, 135)
    private var allLeaguesWithFixtures: List<LeagueWithFixtures> = emptyList()


    fun fetchUpcomingFixtures() {
        viewModelScope.launch {
            _leaguesWithFixtures.value = Result.Loading
            try {
                allLeaguesWithFixtures = topFiveLeagues.mapNotNull { leagueId ->
                    when (val result = repository.getUpcomingFixtures(leagueId)) {
                        is Result.Success -> LeagueWithFixtures(
                            id = leagueId,
                            name = getLeagueName(leagueId),
                            country = getLeagueCountry(leagueId),
                            flag = getLeagueFlag(leagueId),
                            fixtures = result.data
                        )
                        is Result.Error -> null
                        Result.Loading -> TODO()
                    }
                }
                if (allLeaguesWithFixtures.isEmpty()) {
                    _leaguesWithFixtures.value = Result.Error(Exception("No data available. Please check your internet connection."))
                } else {
                    _leaguesWithFixtures.value = Result.Success(allLeaguesWithFixtures)
                }
            } catch (e: Exception) {
                _leaguesWithFixtures.value = Result.Error(e)
            }
        }
    }

    class Factory(private val repository: FootballRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CalendarViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }


    fun selectLeague(leagueId: Int) {
        val selectedLeague = allLeaguesWithFixtures.find { it.id == leagueId }
        selectedLeague?.let {
            _leaguesWithFixtures.value = Result.Success(listOf(it))
        }
    }

    fun selectAllLeagues() {
        _leaguesWithFixtures.value = Result.Success(allLeaguesWithFixtures)
    }

    // Helper functions to get league details (implement these based on your data source)
    private fun getLeagueName(leagueId: Int): String = TODO()
    private fun getLeagueCountry(leagueId: Int): String = TODO()
    private fun getLeagueFlag(leagueId: Int): String = TODO()
}