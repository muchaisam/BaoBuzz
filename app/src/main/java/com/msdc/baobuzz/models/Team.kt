package com.msdc.baobuzz.models

import androidx.compose.runtime.Stable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

// ✅ @Stable because leagueId is mutable (var)
@Stable
@Serializable
@Entity(tableName = "teams")
data class Team(
    @PrimaryKey val id: Int,
    val name: String,
    val code: String?,
    val country: String,
    val founded: Int?,
    val national: Boolean,
    val logo: String,
    val shortName: String? = null,
    // Add this field for database relationship
    var leagueId: Int = 0  // Default value of 0
)
