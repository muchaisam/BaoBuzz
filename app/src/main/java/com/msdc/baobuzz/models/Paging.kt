package com.msdc.baobuzz.models

import kotlinx.serialization.Serializable

@Serializable
data class Paging(
    val current: Int,
    val total: Int
)