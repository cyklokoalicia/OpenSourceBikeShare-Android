package com.bikeshare.app.data.api

import com.bikeshare.app.data.api.dto.ApiEnvelope
import com.bikeshare.app.data.api.dto.AuthTokens
import com.bikeshare.app.data.api.dto.RefreshRequest
import com.bikeshare.app.data.local.TokenStorage
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject

class TokenRefreshAuthenticator @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val moshi: Moshi,
) : Authenticator {

    private val refreshClient: OkHttpClient by lazy { OkHttpClient.Builder().build() }

    @Synchronized
    override fun authenticate(route: Route?, response: Response): Request? {
        // Only retry once
        if (response.request.header("X-Retry") != null) return null

        val refreshToken = tokenStorage.getRefreshToken() ?: return null

        val newTokens = refreshAccessToken(refreshToken, response.request)
        return if (newTokens != null) {
            tokenStorage.saveTokens(
                newTokens.accessToken,
                newTokens.refreshToken,
                newTokens.phoneConfirmed != false,
            )
            response.request.newBuilder()
                .header("Authorization", "Bearer ${newTokens.accessToken}")
                .header("X-Retry", "true")
                .build()
        } else {
            tokenStorage.clearTokens()
            null
        }
    }

    private fun refreshAccessToken(refreshToken: String, originalRequest: Request): AuthTokens? {
        return try {
            val refreshBody = moshi.adapter(RefreshRequest::class.java)
                .toJson(RefreshRequest(refreshToken))

            val request = Request.Builder()
                .url(
                    originalRequest.url.newBuilder()
                        .encodedPath("/api/v1/auth/refresh")
                        .build()
                )
                .post(refreshBody.toRequestBody("application/json".toMediaType()))
                .build()

            val response = refreshClient.newCall(request).execute()

            if (response.isSuccessful) {
                val type = Types.newParameterizedType(
                    ApiEnvelope::class.java,
                    AuthTokens::class.java,
                )
                val adapter = moshi.adapter<ApiEnvelope<AuthTokens>>(type)
                val body = response.body?.string()
                body?.let { adapter.fromJson(it)?.data }
            } else {
                Timber.w("Token refresh failed with status ${response.code}")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Token refresh error")
            null
        }
    }
}
