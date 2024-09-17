package com.msdc.baobuzz.models

import kotlinx.serialization.Serializable

@Serializable
data class Venue(
    val id: Int?,
    val name: String?,
    val city: String?
)