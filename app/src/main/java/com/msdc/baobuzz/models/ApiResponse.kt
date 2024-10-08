package com.msdc.baobuzz.models

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val get: String,
    val parameters: Map<String, String>,
    val errors: List<String>,
    val results: Int,
    val paging: Paging,
    val response: List<T>
)