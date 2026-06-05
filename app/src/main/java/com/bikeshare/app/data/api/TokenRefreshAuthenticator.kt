package com.bikeshare.app.data.api

import com.bikeshare.app.BuildConfig
import com.bikeshare.app.data.api.dto.ApiEnvelope
import com.bikeshare.app.data.api.dto.AuthTokens
import com.bikeshare.app.data.api.dto.RefreshRequest
import com.bikeshare.app.data.local.TokenStorage
import com.bikeshare.app.util.SessionEvent
import com.bikeshare.app.util.SessionEventBus
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.Authenticator
import okhttp3.HttpUrl
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
    private val sessionEventBus: SessionEventBus,
) : Authenticator {

    private val refreshClient: OkHttpClient by lazy { OkHttpClient.Builder().build() }

    @Synchronized
    override fun authenticate(route: Route?, response: Response): Request? {
        // Only retry once
        if (response.request.header("X-Retry") != null) return null

        val refreshToken = tokenStorage.getRefreshToken()
        if (refreshToken == null) {
            // No refresh token at all → the session is genuinely gone.
            endSession()
            return null
        }

        val refresh = runRefresh(refreshToken, response.request.url)

        return when (decideOutcome(refresh.code, refresh.tokens)) {
            Outcome.RETRY -> {
                val tokens = refresh.tokens!!
                tokenStorage.saveTokens(tokens.accessToken, tokens.refreshToken)
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${tokens.accessToken}")
                    .header("X-Retry", "true")
                    .build()
            }
            // 401/403: the refresh token itself was rejected → the session is over.
            Outcome.LOGOUT -> {
                endSession()
                null
            }
            // 426: this build is below the server's version floor (spec 0005). Surface the
            // force-update screen instead of silently wiping a valid session.
            Outcome.UPGRADE -> {
                sessionEventBus.emit(SessionEvent.UpdateRequired)
                null
            }
            // Transient failure (network, 5xx, unparseable 2xx): keep the refresh token so a
            // later request can recover. This one request just fails for now.
            Outcome.KEEP -> null
        }
    }

    /** Clear the stored tokens and tell the UI to route to login (spec 0015). */
    private fun endSession() {
        tokenStorage.clearTokens()
        sessionEventBus.emit(SessionEvent.SessionExpired)
    }

    private fun runRefresh(refreshToken: String, originalUrl: HttpUrl): RefreshResponse {
        return try {
            refreshClient.newCall(buildRefreshRequest(originalUrl, refreshToken)).execute().use { response ->
                val tokens = if (response.isSuccessful) {
                    val type = Types.newParameterizedType(ApiEnvelope::class.java, AuthTokens::class.java)
                    val adapter = moshi.adapter<ApiEnvelope<AuthTokens>>(type)
                    response.body?.string()?.let { adapter.fromJson(it)?.data }
                } else {
                    Timber.w("Token refresh failed with status ${response.code}")
                    null
                }
                RefreshResponse(response.code, tokens)
            }
        } catch (e: Exception) {
            Timber.e(e, "Token refresh error")
            RefreshResponse(code = null, tokens = null)
        }
    }

    private fun buildRefreshRequest(originalUrl: HttpUrl, refreshToken: String): Request {
        val refreshBody = moshi.adapter(RefreshRequest::class.java)
            .toJson(RefreshRequest(refreshToken))
        return Request.Builder()
            .url(originalUrl.newBuilder().encodedPath(REFRESH_PATH).build())
            // The refresh runs on a bare client (no shared interceptors), so it must carry
            // the same version User-Agent the main client sends — otherwise the server's
            // force-update gate (spec 0005) reads it as "0.0.0" and returns 426, which would
            // wipe the session on every token expiry (spec 0015).
            .header("User-Agent", USER_AGENT)
            .post(refreshBody.toRequestBody("application/json".toMediaType()))
            .build()
    }

    private data class RefreshResponse(val code: Int?, val tokens: AuthTokens?)

    enum class Outcome { RETRY, LOGOUT, UPGRADE, KEEP }

    companion object {
        private const val REFRESH_PATH = "/api/v1/auth/refresh"

        /** Same format the main client sends (`NetworkModule`), so the version gate sees the real version. */
        fun userAgent(versionName: String, versionCode: Int): String =
            "${BuildConfig.APP_NAME}-Android/$versionName ($versionCode)"

        val USER_AGENT: String = userAgent(BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)

        /**
         * Map a refresh attempt to what we do with the session:
         * - success carrying tokens → [Outcome.RETRY]
         * - 401/403 (refresh token rejected) → [Outcome.LOGOUT] (clear + route to login)
         * - 426 (below the version floor, spec 0005) → [Outcome.UPGRADE] (force-update, keep tokens)
         * - anything else: 5xx, network error (`code == null`), or a 2xx without a parseable
         *   body → [Outcome.KEEP] (do not discard a still-valid refresh token)
         */
        fun decideOutcome(code: Int?, tokens: AuthTokens?): Outcome = when {
            tokens != null -> Outcome.RETRY
            code == 401 || code == 403 -> Outcome.LOGOUT
            code == 426 -> Outcome.UPGRADE
            else -> Outcome.KEEP
        }
    }
}
