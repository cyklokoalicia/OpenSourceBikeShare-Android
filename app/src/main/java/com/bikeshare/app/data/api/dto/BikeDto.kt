package com.bikeshare.app.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BikeDetailDto(
    @Json(name = "bikeNumber") val bikeNumber: Int,
    @Json(name = "currentUser") val currentUser: String? = null,
    @Json(name = "currentStand") val currentStand: String? = null,
    @Json(name = "note") val note: String? = null,
)

@JsonClass(generateAdapter = true)
data class BikeLastUsageDto(
    @Json(name = "bikeNumber") val bikeNumber: Int,
    @Json(name = "userName") val userName: String? = null,
    @Json(name = "action") val action: String? = null,
    @Json(name = "standName") val standName: String? = null,
    @Json(name = "timestamp") val timestamp: String? = null,
)

@JsonClass(generateAdapter = true)
data class BikeTripPointDto(
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "standName") val standName: String? = null,
    @Json(name = "timestamp") val timestamp: String? = null,
)

@JsonClass(generateAdapter = true)
data class SetLockCodeRequest(
    @Json(name = "code") val code: String,
)
