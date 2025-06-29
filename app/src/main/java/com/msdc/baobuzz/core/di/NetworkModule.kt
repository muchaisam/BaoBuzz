package com.msdc.baobuzz.core.di

import com.msdc.baobuzz.core.api.AuthInterceptor
import com.msdc.baobuzz.core.api.RequestLimitInterceptor
import com.msdc.baobuzz.core.api.FootballRepository
import com.msdc.baobuzz.core.api.FootballRepositoryImpl
import com.msdc.baobuzz.interfaces.FootballApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

        private const val BASE_URL = "https://v3.football.api-sports.io/"

        @Provides
        @Singleton
        fun provideOkHttpClient(
                authInterceptor: AuthInterceptor,
                requestLimitInterceptor: RequestLimitInterceptor
        ): OkHttpClient {
                return OkHttpClient.Builder()
                        .addInterceptor(authInterceptor)
                        .addInterceptor(requestLimitInterceptor)
                        .addInterceptor(
                                HttpLoggingInterceptor().apply {
                                        level = HttpLoggingInterceptor.Level.BODY
                                }
                        )
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .build()
        }

        @Provides
        @Singleton
        fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
                return Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
        }

    @Provides
    @Singleton
    fun provideFootballApi(retrofit: Retrofit): FootballApi {
        return retrofit.create(FootballApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFootballRepository(impl: FootballRepositoryImpl): FootballRepository {
        return impl
    }
}
