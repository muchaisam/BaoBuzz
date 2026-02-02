package com.msdc.baobuzz.models

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

// ✅ @Immutable - all properties are immutable (val)
@Immutable
@Serializable
@Entity(tableName = "leagues")
data class League(
    @PrimaryKey val id: Int,
    val name: String,
    val type: String,
    val country: String?,
    val logo: String,
    val flag: String?,
    val season: Int?,
    val round: String?
)