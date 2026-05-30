package com.bikeshare.app.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

enum class ThemeMode { SYSTEM, LIGHT, DARK }

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("bikeshare_user_prefs", Context.MODE_PRIVATE)

    var themeMode: ThemeMode
        get() {
            val name = prefs.getString(KEY_THEME_MODE, null) ?: return ThemeMode.SYSTEM
            return ThemeMode.entries.firstOrNull { it.name == name } ?: ThemeMode.SYSTEM
        }
        set(value) {
            prefs.edit().putString(KEY_THEME_MODE, value.name).apply()
        }

    // Survives the Activity recreation that AppCompatDelegate.setApplicationLocales
    // triggers, so the confirmation snackbar renders in the newly-applied locale.
    var languageChangePending: Boolean
        get() = prefs.getBoolean(KEY_LANGUAGE_CHANGE_PENDING, false)
        set(value) {
            prefs.edit().putBoolean(KEY_LANGUAGE_CHANGE_PENDING, value).apply()
        }

    fun themeModeFlow(): Flow<ThemeMode> = callbackFlow {
        trySend(themeMode)
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_THEME_MODE) trySend(themeMode)
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.distinctUntilChanged()

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_LANGUAGE_CHANGE_PENDING = "language_change_pending"
    }
}
