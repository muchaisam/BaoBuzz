package com.msdc.baobuzz.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_top_scorers")
data class CachedTopScorers(
    @PrimaryKey val id: String,
    val leagueId: Int,
    val season: Int,
    val topScorersJson: String,
    val lastUpdated: Long
)