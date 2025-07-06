package com.msdc.baobuzz.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msdc.baobuzz.models.Coach

@Dao
interface CoachDao {
    @Query("SELECT * FROM coach WHERE id IN (:ids)")
    suspend fun getCoachesByIds(ids: List<Int>): List<Coach>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoaches(coaches: List<Coach>)
}