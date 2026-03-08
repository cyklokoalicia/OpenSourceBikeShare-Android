package com.bikeshare.app.data.api

import com.bikeshare.app.data.local.TokenStorage
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Skip auth header for token & refresh endpoints
        val path = request.url.encodedPath
        if (path.endsWith("/auth/token") || path.endsWith("/auth/refresh")) {
            return chain.proceed(request)
        }

        val token = runBlocking { tokenStorage.getAccessToken() }

        val authenticatedRequest = if (token != null) {
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }

        return chain.proceed(authenticatedRequest)
    }
}
