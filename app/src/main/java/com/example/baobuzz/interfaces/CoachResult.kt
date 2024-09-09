package com.example.baobuzz.interfaces

sealed class CoachResult<out T> {
    data class Success<out T>(val data: T) : CoachResult<T>()
    data class Error<T>(val exception: Exception) : CoachResult<T>()
}
