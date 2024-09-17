package com.msdc.baobuzz.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msdc.baobuzz.models.Coach

@Dao
interface CoachDao {
    @Query("SELECT * FROM coach WHERE id = :id")
    fun getCoach(id: Int): Coach?

    @Query("SELECT * FROM coach")
    fun getAllCoaches(): List<Coach>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoach(coach: Coach)
}