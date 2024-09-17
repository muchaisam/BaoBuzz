package com.msdc.baobuzz.models

import kotlinx.serialization.Serializable

@Serializable
data class Teams(
    val home: Team,
    val away: Team
)