package com.msdc.baobuzz.models

import kotlinx.serialization.Serializable

@Serializable
data class Country(
    val name: String,
    val code: String?,
    val flag: String?
)