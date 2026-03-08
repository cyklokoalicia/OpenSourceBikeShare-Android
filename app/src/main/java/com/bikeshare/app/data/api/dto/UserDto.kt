package com.bikeshare.app.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "userId") val userId: Int,
    @Json(name = "username") val username: String? = null,
    @Json(name = "city") val city: String? = null,
    @Json(name = "mail") val mail: String? = null,
    @Json(name = "number") val number: String? = null,
    @Json(name = "privileges") val privileges: Int? = null,
    @Json(name = "credit") val credit: Double? = null,
    @Json(name = "userLimit") val userLimit: Int? = null,
    @Json(name = "isNumberConfirmed") val isNumberConfirmed: Int? = null,
    @Json(name = "registrationDate") val registrationDate: String? = null,
)

@JsonClass(generateAdapter = true)
data class UserLimitsDto(
    @Json(name = "limit") val limit: Int? = null,
    @Json(name = "rented") val rented: Int? = null,
    @Json(name = "userCredit") val userCredit: Double? = null,
)

@JsonClass(generateAdapter = true)
data class ChangeCityRequest(
    @Json(name = "city") val city: String,
)

@JsonClass(generateAdapter = true)
data class AddCreditRequest(
    @Json(name = "multiplier") val multiplier: Int,
)
