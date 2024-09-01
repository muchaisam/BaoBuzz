package com.example.baobuzz.daos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.baobuzz.daos.Transfer
import com.example.baobuzz.entity.CachedStanding
import java.util.Date

@Database(entities = [CachedFixture::class, Transfer::class, CachedStanding::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fixtureDao(): FixtureDao
    abstract fun transferDao(): TransferDao

    abstract fun standingsDao(): StandingsDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "football_database"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}

