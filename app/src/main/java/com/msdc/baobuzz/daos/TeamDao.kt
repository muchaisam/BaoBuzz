package com.msdc.baobuzz.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msdc.baobuzz.models.Team
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {
    @Query("SELECT * FROM teams")
    fun getAllTeams(): List<Team>

    @Query("SELECT * FROM teams")
    fun getAllTeamsFlow(): Flow<List<Team>>

    @Query("SELECT * FROM teams WHERE id IN (:teamIds)")
    fun getTeamsByIds(teamIds: List<Int>): List<Team>

    @Query("SELECT * FROM teams WHERE leagueId = :leagueId")
    fun getTeamsForLeague(leagueId: Int): List<Team>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(teams: List<Team>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(team: Team)

    @Delete
    fun delete(team: Team)

    @Query("DELETE FROM teams")
    fun deleteAll()

    @Query("SELECT * FROM teams WHERE name LIKE :searchQuery")
    fun searchTeams(searchQuery: String): List<Team>
}