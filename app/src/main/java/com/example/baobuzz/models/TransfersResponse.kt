package com.example.baobuzz.models

data class ApiTransfer(
    val player: Player,
    val update: String,
    val transfers: List<TransferDetail>
)

data class Player(
    val id: Int,
    val name: String
)

data class TransferDetail(
    val date: String,
    val type: String,
    val teams: TeamsTransfer
)

data class TeamsTransfer(
    val `in`: TeamTransfer,
    val out: TeamTransfer
)

data class TeamTransfer(
    val id: Int,
    val name: String,
    val logo: String
)

data class TransfersResponse(
    val get: String,
    val parameters: Map<String, String>,
    val errors: Map<String, String>?,
    val results: Int,
    val paging: Paging,
    val response: List<ApiTransfer>
)

data class Paging(
    val current: Int,
    val total: Int
)