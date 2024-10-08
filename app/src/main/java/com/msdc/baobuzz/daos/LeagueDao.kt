package com.msdc.baobuzz.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msdc.baobuzz.models.League
import kotlinx.coroutines.flow.Flow

@Dao
interface LeagueDao {
    @Query("SELECT * FROM leagues")
    fun getAllLeagues(): List<League>

    @Query("SELECT * FROM leagues")
    fun getAllLeaguesFlow(): Flow<List<League>>

    @Query("SELECT * FROM leagues WHERE id IN (:leagueIds)")
    fun getLeaguesByIds(leagueIds: List<Int>): List<League>

    @Query("SELECT * FROM leagues ORDER BY name ASC LIMIT 5")
    fun getTopFiveLeagues(): List<League>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(leagues: List<League>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(league: League)

    @Delete
    fun delete(league: League)

    @Query("DELETE FROM leagues")
    fun deleteAll()

    @Query("SELECT * FROM leagues WHERE name LIKE :searchQuery OR country LIKE :searchQuery")
    fun searchLeagues(searchQuery: String): List<League>
}