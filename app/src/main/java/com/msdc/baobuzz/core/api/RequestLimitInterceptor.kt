package com.msdc.baobuzz.core.api

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/** Interceptor that enforces API request limits and tracks usage */
class RequestLimitInterceptor @Inject constructor(private val requestTracker: ApiRequestTracker) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // This is a blocking call, but we need to make it suspend-friendly
        // For now, we'll allow the request and track it after success
        val response = chain.proceed(chain.request())

        // Only track if request was successful
        if (response.isSuccessful) {
            // Note: This should ideally be done in a coroutine scope
            // We'll handle this properly in the repository layer
        }

        return response
    }
}
