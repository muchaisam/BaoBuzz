package com.msdc.baobuzz.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msdc.baobuzz.models.CachedTopAssisters

@Dao
interface TopAssistersDao {
    @Query("SELECT * FROM cached_top_assisters WHERE leagueId = :leagueId AND season = :season")
    fun getTopAssisters(leagueId: Int, season: Int): CachedTopAssisters?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTopAssisters(cachedTopAssisters: CachedTopAssisters)
}