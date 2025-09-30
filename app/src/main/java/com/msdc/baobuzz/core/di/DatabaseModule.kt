package com.msdc.baobuzz.core.di

import android.content.Context
import androidx.room.Room
import com.msdc.baobuzz.core.database.AppCacheDatabase
import com.msdc.baobuzz.core.database.dao.CacheDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppCacheDatabase(@ApplicationContext context: Context): AppCacheDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppCacheDatabase::class.java,
            AppCacheDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideCacheDao(database: AppCacheDatabase): CacheDao {
        return database.cacheDao()
    }
}
