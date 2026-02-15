package com.msdc.baobuzz.core.models

import androidx.compose.runtime.Immutable

// ✅ Transfer models marked @Immutable for Compose performance

@Immutable
data class TransferDetails(
    val id: String = "", // Added for list keys
    val date: String,
    val type: String,
    val teamIn: TeamDetails,
    val teamOut: TeamDetails,
    val player: PlayerDetails
)

@Immutable
data class TeamDetails(val id: Int, val name: String, val logo: String)

@Immutable
data class PlayerDetails(val id: Int, val name: String, val photo: String? = null)
