package com.msdc.baobuzz.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
data class LeagueWrapper(
    val league: League,
    val country: Country,
    val seasons: List<Season>
)
