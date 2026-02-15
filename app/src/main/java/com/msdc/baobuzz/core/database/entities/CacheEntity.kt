package com.msdc.baobuzz.core.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Generic cache entity for storing any type of football data
 * Uses JSON serialization to store complex objects
 */
@Entity(
    tableName = "football_cache",
    indices = [
        Index("dataType"),
        Index("dataType", "lastUpdated")
    ]
)
data class CacheEntity(
    @PrimaryKey
    val cacheKey: String,
    val dataType: String,
    val jsonData: String,
    val lastUpdated: Long,
    val expiryTime: Long,
    val leagueIds: String? = null,
    val additionalParams: String? = null
)
