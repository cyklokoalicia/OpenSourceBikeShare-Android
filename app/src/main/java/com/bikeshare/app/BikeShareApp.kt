package com.bikeshare.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.sentry.SentryLevel
import io.sentry.android.core.SentryAndroid
import io.sentry.android.timber.SentryTimberTree
import timber.log.Timber

@HiltAndroidApp
class BikeShareApp : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        val dsn = BuildConfig.SENTRY_DSN
        if (dsn.isNotBlank()) {
            SentryAndroid.init(this) { options ->
                options.dsn = dsn
                options.isEnableAutoSessionTracking = true
                options.tracesSampleRate = if (BuildConfig.DEBUG) 1.0 else 0.2
                options.environment = if (BuildConfig.DEBUG) "development" else "production"
                options.release = "${BuildConfig.APPLICATION_ID}@${BuildConfig.VERSION_NAME}"
            }
            Timber.plant(SentryTimberTree(SentryLevel.ERROR, SentryLevel.WARNING))
        }
    }
}
