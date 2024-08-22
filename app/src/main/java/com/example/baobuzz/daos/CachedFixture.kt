package com.example.baobuzz.daos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fixtures")
data class CachedFixture(
    @PrimaryKey val id: Int,
    val leagueId: Int,
    val fixtureData: String,
    val lastUpdated: Long
)