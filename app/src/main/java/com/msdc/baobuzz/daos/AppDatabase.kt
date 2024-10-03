package com.msdc.baobuzz.daos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.msdc.baobuzz.entity.CachedStanding
import com.msdc.baobuzz.models.CareerStepListConverter
import com.msdc.baobuzz.models.Coach
import com.msdc.baobuzz.models.League
import com.msdc.baobuzz.models.Team
import com.msdc.baobuzz.models.TeamConverter

@Database(entities = [CachedFixture::class, Transfer::class,
    CachedStanding::class, Coach::class,
    League::class, Team::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(TeamConverter::class, CareerStepListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fixtureDao(): FixtureDao
    abstract fun transferDao(): TransferDao

    abstract fun standingsDao(): StandingsDao

    abstract fun coachDao(): CoachDao

    abstract fun leagueDao(): LeagueDao
    abstract fun teamDao(): TeamDao

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

