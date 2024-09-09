package com.example.baobuzz.daos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.baobuzz.entity.CachedStanding
import com.example.baobuzz.models.CareerStepListConverter
import com.example.baobuzz.models.Coach
import com.example.baobuzz.models.TeamConverter

@Database(entities = [CachedFixture::class, Transfer::class, CachedStanding::class, Coach::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(TeamConverter::class, CareerStepListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fixtureDao(): FixtureDao
    abstract fun transferDao(): TransferDao

    abstract fun standingsDao(): StandingsDao

    abstract fun coachDao(): CoachDao

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

