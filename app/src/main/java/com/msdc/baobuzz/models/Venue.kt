package com.msdc.baobuzz.models

import kotlinx.serialization.Serializable

@Serializable
data class Venue(
    val id: Int,
    val name: String,
    val address: String?,
    val city: String,
    val capacity: Int,
    val surface: String,
    val image: String?
)