package com.example.baobuzz.models

import kotlinx.serialization.Serializable

@Serializable
data class League(
    val id: Int,
    val name: String,
    val country: String,
    val logo: String,
    val flag: String,
    val season: Int,
    val round: String
)