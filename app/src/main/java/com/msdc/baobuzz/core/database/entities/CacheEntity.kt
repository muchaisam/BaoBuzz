package com.msdc.baobuzz.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Generic cache entity for storing any type of football data
 * Uses JSON serialization to store complex objects
 */
@Entity(tableName = "football_cache")
data class CacheEntity(
    @PrimaryKey
    val cacheKey: String,
    val dataType: String, // "live_matches", "standings", "transfers", etc.
    val jsonData: String, // Serialized data
    val lastUpdated: Long, // Timestamp when data was cached
    val expiryTime: Long, // When this cache entry expires
    val leagueIds: String? = null, // Comma-separated league IDs for filtering
    val additionalParams: String? = null // For future extensibility
)
