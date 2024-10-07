package com.msdc.baobuzz.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.msdc.baobuzz.repository.CoachRepository
import com.msdc.baobuzz.viewmodel.CoachViewModel
import javax.inject.Inject

// ViewModelFactory
class CoachViewModelFactory @Inject constructor(
    private val repository: CoachRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CoachViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CoachViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
