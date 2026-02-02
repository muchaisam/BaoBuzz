package com.msdc.baobuzz.core.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.msdc.baobuzz.BuildConfig
import com.msdc.baobuzz.core.api.AuthInterceptor
import com.msdc.baobuzz.core.api.RequestLimitInterceptor
import com.msdc.baobuzz.core.api.interceptors.FootballDataAuthInterceptor
import com.msdc.baobuzz.core.api.interfaces.FootballDataApi
import com.msdc.baobuzz.core.api.interfaces.OpenFootballApi
import com.msdc.baobuzz.interfaces.FootballApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * NetworkModule - Multi-API Architecture 🚀
 *
 * Provides three football APIs:
 * 1. api-sports.io (paid, premium features)
 * 2. OpenFootball (FREE, historical data from GitHub)
 * 3. football-data.org (FREE, 10 calls/min for live data)
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // API Base URLs
    private const val API_SPORTS_URL = "https://v3.football.api-sports.io/"
    private const val OPEN_FOOTBALL_URL = "https://raw.githubusercontent.com/openfootball/football.json/master/"
    private const val FOOTBALL_DATA_URL = "https://api.football-data.org/v4/"

    // ================== Shared Dependencies ==================

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    // ================== API-Sports.io (Existing, Premium) ==================

    @Provides
    @Singleton
    @Named("api-sports")
    fun provideApiSportsOkHttpClient(
        authInterceptor: AuthInterceptor,
        requestLimitInterceptor: RequestLimitInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(requestLimitInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("api-sports")
    fun provideApiSportsRetrofit(
        @Named("api-sports") okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(API_SPORTS_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideFootballApi(@Named("api-sports") retrofit: Retrofit): FootballApi {
        return retrofit.create(FootballApi::class.java)
    }

    // ================== OpenFootball API (FREE, Historical) ==================

    @Provides
    @Singleton
    @Named("openfootball")
    fun provideOpenFootballOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS) // Longer timeout for GitHub
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("openfootball")
    fun provideOpenFootballRetrofit(
        @Named("openfootball") okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(OPEN_FOOTBALL_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenFootballApi(@Named("openfootball") retrofit: Retrofit): OpenFootballApi {
        return retrofit.create(OpenFootballApi::class.java)
    }

    // ================== Football-Data.org API (FREE, Live Data) ==================

    @Provides
    @Singleton
    fun provideFootballDataAuthInterceptor(): FootballDataAuthInterceptor {
        // API key will be loaded from BuildConfig (local.properties)
        // For now, using a placeholder that can be replaced
        val apiKey = try {
            // Try to get from BuildConfig if available
            val buildConfigClass = Class.forName("com.msdc.baobuzz.BuildConfig")
            val field = buildConfigClass.getField("FOOTBALL_DATA_API_KEY")
            field.get(null) as? String ?: ""
        } catch (e: Exception) {
            "" // Empty string if not configured yet
        }
        return FootballDataAuthInterceptor(apiKey)
    }

    @Provides
    @Singleton
    @Named("football-data")
    fun provideFootballDataOkHttpClient(
        footballDataAuthInterceptor: FootballDataAuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(footballDataAuthInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("football-data")
    fun provideFootballDataRetrofit(
        @Named("football-data") okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(FOOTBALL_DATA_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("football-data")
    fun provideFootballDataApi(@Named("football-data") retrofit: Retrofit): FootballDataApi {
        return retrofit.create(FootballDataApi::class.java)
    }

    // ================== Backward Compatibility ==================

    // Keep these for existing code that uses unnamed OkHttpClient/Retrofit
    @Provides
    @Singleton
    fun provideOkHttpClient(
        @Named("api-sports") okHttpClient: OkHttpClient
    ): OkHttpClient = okHttpClient

    @Provides
    @Singleton
    fun provideRetrofit(
        @Named("api-sports") retrofit: Retrofit
    ): Retrofit = retrofit
}
