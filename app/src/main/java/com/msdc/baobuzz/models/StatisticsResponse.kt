package com.msdc.baobuzz.models


data class StatisticsResponse(
    val response: List<Statistic>
)

data class LineupsResponse(
    val response: List<Lineup>
)

data class PredictionsResponse(
    val response: List<Prediction>
)