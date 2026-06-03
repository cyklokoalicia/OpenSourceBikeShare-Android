package com.bikeshare.app.data.api

import com.bikeshare.app.util.SessionEvent
import com.bikeshare.app.util.SessionEventBus
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Detects the server's force-update gate — a `426 Upgrade Required` returned when the
 * client is below the configured minimum supported version (spec 0005) — and emits a
 * [SessionEvent.UpdateRequired] so the UI can block with the force-update screen. The
 * status code alone is unambiguous, so (unlike [PhoneUnconfirmedInterceptor]) the body
 * is not inspected.
 */
class UpgradeRequiredInterceptor @Inject constructor(
    private val sessionEventBus: SessionEventBus,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code == HTTP_UPGRADE_REQUIRED) {
            sessionEventBus.emit(SessionEvent.UpdateRequired)
        }
        return response
    }

    companion object {
        private const val HTTP_UPGRADE_REQUIRED = 426
    }
}
