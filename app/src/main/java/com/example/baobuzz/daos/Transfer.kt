package com.example.baobuzz.daos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transfers")
data class Transfer(
    @PrimaryKey val id: Int,
    val teamId: Int,
    val playerName: String,
    val fromTeam: String,
    val toTeam: String,
    val transferDate: String,
    val transferType: String
)