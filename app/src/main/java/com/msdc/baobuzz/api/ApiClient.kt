package com.msdc.baobuzz.api

import com.msdc.baobuzz.BuildConfig
import com.msdc.baobuzz.interfaces.FootballApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Legacy ApiClient - will be replaced by new DI-based NetworkModule TODO: Remove after migration to
 * new architecture
 */
@Deprecated("Use NetworkModule with Hilt injection instead")
object ApiClient {
    private const val BASE_URL = "https://v3.football.api-sports.io/"
    private const val API_HOST = "v3.football.api-sports.io"

    private val okHttpClient =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder =
                    original.newBuilder()
                        .header("x-rapidapi-key", BuildConfig.FOOTBALL_API_KEY)
                        .header("x-rapidapi-host", API_HOST)
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()

    val retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val footballApi: FootballApi = retrofit.create(FootballApi::class.java)
}
