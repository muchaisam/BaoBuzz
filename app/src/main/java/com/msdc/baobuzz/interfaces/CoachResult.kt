package com.msdc.baobuzz.interfaces

sealed class CoachResult<out T> {
    data class Success<out T>(val data: T) : CoachResult<T>()
    data class Error(val exception: Exception) : CoachResult<Nothing>()
    object RateLimit : CoachResult<Nothing>()
}