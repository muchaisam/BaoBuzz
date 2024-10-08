package com.msdc.baobuzz.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msdc.baobuzz.entity.CachedStanding

@Dao
interface StandingsDao {
    @Query("SELECT * FROM cached_standings WHERE leagueId = :leagueId AND season = :season")
    fun getStandings(leagueId: Int, season: Int): CachedStanding?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStandings(cachedStanding: CachedStanding)
}