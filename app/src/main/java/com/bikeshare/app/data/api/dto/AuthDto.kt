package com.bikeshare.app.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TokenRequest(
    @Json(name = "number") val number: String,
    @Json(name = "password") val password: String,
)

@JsonClass(generateAdapter = true)
data class RefreshRequest(
    @Json(name = "refreshToken") val refreshToken: String,
)

@JsonClass(generateAdapter = true)
data class LogoutRequest(
    @Json(name = "refreshToken") val refreshToken: String?,
)

@JsonClass(generateAdapter = true)
data class AuthTokens(
    @Json(name = "accessToken") val accessToken: String,
    @Json(name = "refreshToken") val refreshToken: String,
)
