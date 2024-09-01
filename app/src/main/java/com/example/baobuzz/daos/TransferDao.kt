package com.example.baobuzz.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransferDao {
    @Query("SELECT * FROM transfers WHERE teamId = :teamId AND substr(transferDate, 1, 4) = :year")
    fun getRecentTransfersForTeam(teamId: Int, year: Int): List<Transfer>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransfers(transfers: List<Transfer>)
}