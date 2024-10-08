package com.msdc.baobuzz.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msdc.baobuzz.models.CachedTopScorers

@Dao
interface TopScorersDao {
    @Query("SELECT * FROM cached_top_scorers WHERE leagueId = :leagueId AND season = :season")
    fun getTopScorers(leagueId: Int, season: Int): CachedTopScorers?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTopScorers(cachedTopScorers: CachedTopScorers)
}