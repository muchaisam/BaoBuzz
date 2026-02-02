package com.msdc.baobuzz.core.api.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Authentication interceptor for football-data.org API
 *
 * Adds X-Auth-Token header to all requests
 * API Key is loaded from local.properties (FOOTBALL_DATA_API_KEY)
 *
 * Free tier: 10 requests per minute
 * Register at: https://www.football-data.org/client/register
 */
class FootballDataAuthInterceptor @Inject constructor(
    private val apiKey: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val request = original.newBuilder()
            .header("X-Auth-Token", apiKey)
            .header("Accept", "application/json")
            .method(original.method, original.body)
            .build()

        return chain.proceed(request)
    }
}
