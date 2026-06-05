package com.bikeshare.app.data.api

import com.bikeshare.app.data.api.dto.AuthTokens
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TokenRefreshAuthenticatorTest {

    // The exact regex the server uses to read the Android version off the User-Agent
    // (BikeShare-web ClientVersionDetector.ANDROID_UA_PATTERN). A UA that does not match
    // is treated as "0.0.0" and gated with 426 — which is the bug (spec 0015): the bare
    // refresh client sent the default `okhttp/…` UA, so every refresh was rejected.
    private val serverAndroidUaPattern = Regex("""^.+-Android/(\d+\.\d+\.\d+)\s*\(\d+\)$""")

    @Test
    fun `a release-style User-Agent is parseable by the server version gate`() {
        // A clean release version must match the server pattern, so the gate reads the real
        // version instead of falling back to "0.0.0" (→ 426). Uses a fixed release version so
        // the test does not depend on the build's versionName (which may carry a -debug suffix).
        assertTrue(serverAndroidUaPattern.matches(ApiUserAgent.format("1.1.6", 125)))
    }

    @Test
    fun `the app User-Agent is not okhttp's default`() {
        // The bug (spec 0015): the bare refresh client sent the default `okhttp/…` UA, which
        // the server reads as "0.0.0" and rejects with 426. The shared UA must be the app's.
        assertTrue(
            "UA '${ApiUserAgent.value}' must be the app UA, not okhttp's default",
            ApiUserAgent.value.contains("-Android/"),
        )
        assertFalse(ApiUserAgent.value.startsWith("okhttp/"))
    }

    @Test
    fun `successful refresh with tokens retries the request`() {
        val tokens = AuthTokens(accessToken = "a", refreshToken = "r")
        assertEquals(TokenRefreshAuthenticator.Outcome.RETRY, TokenRefreshAuthenticator.decideOutcome(200, tokens))
    }

    @Test
    fun `rejected refresh token (401 or 403) ends the session`() {
        assertEquals(TokenRefreshAuthenticator.Outcome.LOGOUT, TokenRefreshAuthenticator.decideOutcome(401, null))
        assertEquals(TokenRefreshAuthenticator.Outcome.LOGOUT, TokenRefreshAuthenticator.decideOutcome(403, null))
    }

    @Test
    fun `426 triggers force-update, not logout`() {
        assertEquals(TokenRefreshAuthenticator.Outcome.UPGRADE, TokenRefreshAuthenticator.decideOutcome(426, null))
    }

    @Test
    fun `transient failures keep the tokens`() {
        // 5xx, a network error (null code), and a 2xx with no parseable body must NOT
        // discard a still-valid 30-day refresh token.
        assertEquals(TokenRefreshAuthenticator.Outcome.KEEP, TokenRefreshAuthenticator.decideOutcome(500, null))
        assertEquals(TokenRefreshAuthenticator.Outcome.KEEP, TokenRefreshAuthenticator.decideOutcome(502, null))
        assertEquals(TokenRefreshAuthenticator.Outcome.KEEP, TokenRefreshAuthenticator.decideOutcome(null, null))
        assertEquals(TokenRefreshAuthenticator.Outcome.KEEP, TokenRefreshAuthenticator.decideOutcome(200, null))
    }
}
