package com.msdc.baobuzz.core.models

data class TransferDetails(
    val date: String,
    val type: String,
    val teamIn: TeamDetails,
    val teamOut: TeamDetails,
    val player: PlayerDetails
)

data class TeamDetails(val id: Int, val name: String, val logo: String)

data class PlayerDetails(val id: Int, val name: String, val photo: String? = null)
