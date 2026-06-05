package com.bikeshare.app.data.api

import com.bikeshare.app.BuildConfig

/**
 * The single source of truth for the HTTP `User-Agent` on all app→API traffic. It MUST be
 * sent on every request — including the bare token-refresh client — because the server's
 * force-update gate (spec 0005) reads the app version out of it via the
 * `…-Android/<versionName> (<versionCode>)` shape. A missing or non-versioned UA is read as
 * "0.0.0" and rejected with 426, which previously wiped the session on every token refresh
 * (spec 0015). Keep this the only place the UA string is built.
 */
object ApiUserAgent {

    /** Builds the UA from explicit version parts (kept separate so it is unit-testable). */
    fun format(versionName: String, versionCode: Int): String =
        "${BuildConfig.APP_NAME}-Android/$versionName ($versionCode)"

    /** The UA for this build. */
    val value: String = format(BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
}
