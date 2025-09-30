package com.msdc.baobuzz.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msdc.baobuzz.core.database.entities.CacheEntity

@Dao
interface CacheDao {

    @Query("SELECT * FROM football_cache WHERE cacheKey = :key AND lastUpdated + expiryTime > :currentTime")
    suspend fun getCacheEntry(
        key: String,
        currentTime: Long = System.currentTimeMillis()
    ): CacheEntity?

    @Query("SELECT * FROM football_cache WHERE dataType = :dataType AND lastUpdated + expiryTime > :currentTime")
    suspend fun getCacheEntriesByType(
        dataType: String,
        currentTime: Long = System.currentTimeMillis()
    ): List<CacheEntity>

    @Query("SELECT * FROM football_cache WHERE dataType = :dataType AND leagueIds LIKE '%' || :leagueId || '%' AND lastUpdated + expiryTime > :currentTime")
    suspend fun getCacheEntriesByLeague(
        dataType: String,
        leagueId: Int,
        currentTime: Long = System.currentTimeMillis()
    ): List<CacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCacheEntry(cacheEntry: CacheEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCacheEntries(cacheEntries: List<CacheEntity>)

    @Query("DELETE FROM football_cache WHERE cacheKey = :key")
    suspend fun deleteCacheEntry(key: String)

    @Query("DELETE FROM football_cache WHERE dataType = :dataType")
    suspend fun deleteCacheEntriesByType(dataType: String)

    @Query("DELETE FROM football_cache WHERE lastUpdated + expiryTime <= :currentTime")
    suspend fun deleteExpiredEntries(currentTime: Long = System.currentTimeMillis())

    @Query("DELETE FROM football_cache")
    suspend fun clearAllCache()

    @Query("SELECT COUNT(*) FROM football_cache")
    suspend fun getCacheSize(): Int
}
