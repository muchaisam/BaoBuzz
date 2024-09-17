package com.msdc.baobuzz.models

import kotlinx.serialization.Serializable

@Serializable
data class Goals(
    val home: Int?,
    val away: Int?
)