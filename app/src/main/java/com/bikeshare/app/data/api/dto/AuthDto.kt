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
    @Json(name = "phoneConfirmed") val phoneConfirmed: Boolean? = null,
)

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    @Json(name = "fullname") val fullname: String,
    @Json(name = "city") val city: String,
    @Json(name = "useremail") val useremail: String,
    @Json(name = "password") val password: String,
    @Json(name = "password2") val password2: String,
    @Json(name = "number") val number: String,
    @Json(name = "agree") val agree: Boolean,
)

@JsonClass(generateAdapter = true)
data class RegisterResponse(
    @Json(name = "message") val message: String,
)

@JsonClass(generateAdapter = true)
data class PhoneConfirmRequestResponse(
    @Json(name = "checkCode") val checkCode: String,
)

@JsonClass(generateAdapter = true)
data class PhoneConfirmVerifyRequest(
    @Json(name = "code") val code: String,
    @Json(name = "checkCode") val checkCode: String,
)
