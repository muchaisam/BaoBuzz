package com.msdc.baobuzz.daos

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.msdc.baobuzz.api.ApiClient
import com.msdc.baobuzz.interfaces.FootballApi
import com.msdc.baobuzz.repository.CoachRepository
import com.msdc.baobuzz.repository.LeagueRepository
import com.msdc.baobuzz.repository.TeamRepository
import com.msdc.baobuzz.repository.TransfersRepository
import com.msdc.baobuzz.repository.UserPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFootballApi(): FootballApi = ApiClient.footballApi

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "football_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideCoachDao(database: AppDatabase): CoachDao {
        return database.coachDao()
    }

    @Provides
    @Singleton
    fun provideTransferDao(database: AppDatabase): TransferDao {
        return database.transferDao()
    }

    @Provides
    @Singleton
    @Named("IoDispatcher")
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideTransfersRepository(
        api: FootballApi,
        transfersDao: TransferDao,
        @Named("IoDispatcher") ioDispatcher: CoroutineDispatcher
    ): TransfersRepository {
        return TransfersRepository(api, transfersDao, ioDispatcher)
    }

    @Provides
    @Singleton
    fun provideCoachRepository(api: FootballApi, coachDao: CoachDao): CoachRepository {
        return CoachRepository(api, coachDao)
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideLeagueRepository(api: FootballApi, db: AppDatabase): LeagueRepository {
        return LeagueRepository(api, db)
    }

    @Provides
    @Singleton
    fun provideTeamRepository(api: FootballApi, db: AppDatabase): TeamRepository {
        return TeamRepository(api, db)
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(dataStore: DataStore<Preferences>): UserPreferencesRepository {
        return UserPreferencesRepository(dataStore)
    }
}