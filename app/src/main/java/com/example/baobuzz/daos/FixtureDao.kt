package com.example.baobuzz.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FixtureDao {
    @Query("SELECT * FROM cached_fixtures WHERE league_id = :leagueId")
    fun getFixturesForLeague(leagueId: Int): List<CachedFixture>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFixtures(fixtures: List<CachedFixture>)

    @Query("DELETE FROM cached_fixtures WHERE league_id = :leagueId")
    fun deleteFixturesForLeague(leagueId: Int)
}