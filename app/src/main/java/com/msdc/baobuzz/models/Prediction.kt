package com.msdc.baobuzz.models

import kotlinx.serialization.Serializable

@Serializable
data class Prediction(
    val winner: Team?,
    val winPercentage: WinPercentage,
    val score: PredictedScore
)

@Serializable
data class WinPercentage(
    val home: String,
    val draw: String,
    val away: String
)

@Serializable
data class PredictedScore(
    val home: String,
    val away: String
)
