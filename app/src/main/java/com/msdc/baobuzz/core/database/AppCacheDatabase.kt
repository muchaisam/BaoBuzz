package com.msdc.baobuzz.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.msdc.baobuzz.core.database.dao.CacheDao
import com.msdc.baobuzz.core.database.entities.CacheEntity

/**
 * Room database for persistent caching of football data
 * This is separate from the main app database to keep concerns separated
 */
@Database(
    entities = [CacheEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppCacheDatabase : RoomDatabase() {
    abstract fun cacheDao(): CacheDao

    companion object {
        const val DATABASE_NAME = "football_cache_database"

        @Volatile
        private var instance: AppCacheDatabase? = null

        fun getInstance(context: Context): AppCacheDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppCacheDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppCacheDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
