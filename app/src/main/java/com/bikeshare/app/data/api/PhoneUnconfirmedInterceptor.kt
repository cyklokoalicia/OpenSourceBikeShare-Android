package com.bikeshare.app.data.api

import com.bikeshare.app.util.SessionEvent
import com.bikeshare.app.util.SessionEventBus
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

/**
 * Detects the server's "phone not confirmed" gate — a 403 carrying
 * `code: phone_unconfirmed` (spec 0001) — and emits a [SessionEvent] so the UI can
 * route to the verify screen. The body is peeked (not consumed), so downstream
 * parsing is unaffected. Only this specific code triggers it; a generic 403 does not.
 */
class PhoneUnconfirmedInterceptor @Inject constructor(
    private val sessionEventBus: SessionEventBus,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code == HTTP_FORBIDDEN) {
            val body = response.peekBody(MAX_PEEK_BYTES).string()
            if (isPhoneUnconfirmed(body)) {
                sessionEventBus.emit(SessionEvent.PhoneUnconfirmed)
            }
        }
        return response
    }

    companion object {
        private const val HTTP_FORBIDDEN = 403
        private const val MAX_PEEK_BYTES = 4096L
        const val CODE_PHONE_UNCONFIRMED = "phone_unconfirmed"

        /** True when the (problem+json) error body carries `code = phone_unconfirmed`. */
        fun isPhoneUnconfirmed(body: String?): Boolean {
            if (body.isNullOrBlank()) return false
            return try {
                JSONObject(body).optString("code") == CODE_PHONE_UNCONFIRMED
            } catch (_: JSONException) {
                false
            }
        }
    }
}
