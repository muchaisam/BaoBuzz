package com.example.baobuzz.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "standings")
data class CachedStanding(
    @PrimaryKey val id: String,
    val leagueId: Int,
    val season: Int,
    val standingsJson: String,
    val lastUpdated: Long
)