package com.example.baobuzz.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.baobuzz.entity.CachedStanding

@Dao
interface StandingsDao {
    @Query("SELECT * FROM standings WHERE leagueId = :leagueId AND season = :season")
    fun getStandings(leagueId: Int, season: Int): CachedStanding?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStandings(standing: CachedStanding)
}
