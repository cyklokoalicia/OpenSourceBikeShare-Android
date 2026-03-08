package com.bikeshare.app.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenStorage @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        "bikeshare_secure_prefs",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_PHONE_CONFIRMED = "phone_confirmed"
    }

    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        phoneConfirmed: Boolean = true,
    ) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .putBoolean(KEY_PHONE_CONFIRMED, phoneConfirmed)
            .apply()
    }

    suspend fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    suspend fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    suspend fun getPhoneConfirmed(): Boolean = prefs.getBoolean(KEY_PHONE_CONFIRMED, true)

    suspend fun setPhoneConfirmed(confirmed: Boolean) {
        prefs.edit().putBoolean(KEY_PHONE_CONFIRMED, confirmed).apply()
    }

    suspend fun clearTokens() {
        prefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_PHONE_CONFIRMED)
            .apply()
    }

    suspend fun hasTokens(): Boolean = getAccessToken() != null
}
