package com.msdc.baobuzz.models

import kotlinx.serialization.Serializable

data class Match(
    val id: Int,
    val homeTeam: Team,
    val awayTeam: Team,
    val date: String,
    val status : String,
    val league: String,
    val score : Score
)

@Serializable
data class Score(
    val halftime: Goals?,
    val fulltime: Goals?,
    val extratime: Goals?,
    val penalty: Goals?
)