package com.example.baobuzz.daos

import android.content.Context
import androidx.room.Room
import com.example.baobuzz.api.ApiClient
import com.example.baobuzz.interfaces.FootballApi
import com.example.baobuzz.repository.CoachRepository
import com.example.baobuzz.repository.TransfersRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Singleton

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
}