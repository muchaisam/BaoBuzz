package com.msdc.baobuzz.core.api

import com.msdc.baobuzz.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/** Interceptor that adds authentication headers to API requests */
class AuthInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder =
            original.newBuilder()
                .header("x-rapidapi-key", BuildConfig.FOOTBALL_API_KEY)
                .header("x-rapidapi-host", "v3.football.api-sports.io")

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
