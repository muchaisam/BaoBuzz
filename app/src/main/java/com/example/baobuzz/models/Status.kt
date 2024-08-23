package com.example.baobuzz.models

import kotlinx.serialization.Serializable

@Serializable
data class Status(
    val long: String,
    val short: String,
    val elapsed: Int?
)