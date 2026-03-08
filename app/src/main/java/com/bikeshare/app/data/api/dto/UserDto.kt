package com.bikeshare.app.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "userId") val userId: Int,
    @Json(name = "userName") val userName: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "city") val city: String? = null,
    @Json(name = "privileges") val privileges: Int? = null,
    @Json(name = "credit") val credit: Double? = null,
    @Json(name = "limit") val limit: Int? = null,
)

@JsonClass(generateAdapter = true)
data class UserLimitsDto(
    @Json(name = "limit") val limit: Int? = null,
    @Json(name = "remaining") val remaining: Int? = null,
    @Json(name = "credit") val credit: Double? = null,
    @Json(name = "creditCurrency") val creditCurrency: String? = null,
)

@JsonClass(generateAdapter = true)
data class ChangeCityRequest(
    @Json(name = "city") val city: String,
)

@JsonClass(generateAdapter = true)
data class AddCreditRequest(
    @Json(name = "multiplier") val multiplier: Int,
)
