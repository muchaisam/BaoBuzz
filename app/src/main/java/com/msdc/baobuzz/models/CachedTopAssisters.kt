package com.msdc.baobuzz.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_top_assisters")
data class CachedTopAssisters(
    @PrimaryKey val id: String,
    val leagueId: Int,
    val season: Int,
    val topAssistersJson: String,
    val lastUpdated: Long
)