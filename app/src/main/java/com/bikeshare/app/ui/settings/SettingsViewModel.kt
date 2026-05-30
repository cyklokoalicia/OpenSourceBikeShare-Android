package com.bikeshare.app.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import com.bikeshare.app.data.local.ThemeMode
import com.bikeshare.app.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// `tag = null` clears the per-app locale override (follow system).
enum class AppLanguage(val tag: String?) {
    SYSTEM(null),
    ENGLISH("en"),
    SLOVAK("sk"),
    UKRAINIAN("uk"),
}

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.SYSTEM,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            themeMode = userPreferences.themeMode,
            language = currentLanguage(),
        ),
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        userPreferences.themeMode = mode
        _uiState.value = _uiState.value.copy(themeMode = mode)
    }

    fun setLanguage(language: AppLanguage) {
        if (language == _uiState.value.language) return
        val locales = language.tag?.let { LocaleListCompat.forLanguageTags(it) }
            ?: LocaleListCompat.getEmptyLocaleList()
        userPreferences.languageChangePending = true
        AppCompatDelegate.setApplicationLocales(locales)
        _uiState.value = _uiState.value.copy(language = language)
    }

    fun consumeLanguageChangePending(): Boolean {
        val pending = userPreferences.languageChangePending
        if (pending) userPreferences.languageChangePending = false
        return pending
    }

    private fun currentLanguage(): AppLanguage {
        val current = AppCompatDelegate.getApplicationLocales()
        if (current.isEmpty) return AppLanguage.SYSTEM
        val tag = current[0]?.language ?: return AppLanguage.SYSTEM
        return AppLanguage.entries.firstOrNull { it.tag == tag } ?: AppLanguage.SYSTEM
    }
}
