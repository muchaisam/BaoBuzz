package com.example.baobuzz.models

import kotlinx.serialization.Serializable

@Serializable
data class Goals(
    val home: Int?,
    val away: Int?
)