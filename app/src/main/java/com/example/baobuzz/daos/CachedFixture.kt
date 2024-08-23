package com.example.baobuzz.daos

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_fixtures")
data class CachedFixture(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "league_id") val leagueId: Int,
    @ColumnInfo(name = "fixture_data") val fixtureData: String,
    @ColumnInfo(name = "fixture_date") val fixtureDate: Long,
    @ColumnInfo(name = "last_updated") val lastUpdated: Long
)