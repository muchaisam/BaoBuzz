package com.example.baobuzz.models

import kotlinx.serialization.Serializable

@Serializable
data class Periods(
    val first: Long?,
    val second: Long?
)