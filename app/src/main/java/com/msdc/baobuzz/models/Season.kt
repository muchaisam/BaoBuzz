package com.msdc.baobuzz.models

import kotlinx.serialization.Serializable

@Serializable
data class Season(
    val year: Int,
    val start: String,
    val end: String,
    val current: Boolean
)