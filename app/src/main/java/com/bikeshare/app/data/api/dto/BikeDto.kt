package com.bikeshare.app.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BikeDetailDto(
    @Json(name = "bikeNum") val bikeNum: Int,
    @Json(name = "userId") val userId: Int? = null,
    @Json(name = "userName") val userName: String? = null,
    @Json(name = "standName") val standName: String? = null,
    @Json(name = "isServiceStand") val isServiceStand: Boolean? = null,
    @Json(name = "notes") val notes: String? = null,
    @Json(name = "rentTime") val rentTime: String? = null,
)

@JsonClass(generateAdapter = true)
data class BikeLastUsageDto(
    @Json(name = "notes") val notes: String? = null,
    @Json(name = "history") val history: List<BikeHistoryItemDto> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class BikeHistoryItemDto(
    @Json(name = "time") val time: String? = null,
    @Json(name = "action") val action: String? = null,
    @Json(name = "userName") val userName: String? = null,
    @Json(name = "standName") val standName: String? = null,
    @Json(name = "parameter") val parameter: String? = null,
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
