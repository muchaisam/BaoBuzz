package com.msdc.baobuzz.models

import kotlinx.serialization.Serializable

@Serializable
data class Team(
    val id: Int,
    val name: String,
    val logo: String,
    val winner: Boolean?
)