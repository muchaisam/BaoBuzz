package com.msdc.baobuzz.core.di

import com.msdc.baobuzz.core.api.FootballRepository
import com.msdc.baobuzz.core.api.FootballRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FootballModule {

    @Binds
    @Singleton
    abstract fun bindFootballRepository(
        footballRepositoryImpl: FootballRepositoryImpl
    ): FootballRepository
}
