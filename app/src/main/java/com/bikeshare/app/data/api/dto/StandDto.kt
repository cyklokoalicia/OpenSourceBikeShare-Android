package com.bikeshare.app.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StandMarkerDto(
    @Json(name = "standId") val standId: Int? = null,
    @Json(name = "standName") val standName: String,
    @Json(name = "standDescription") val standDescription: String? = null,
    @Json(name = "standPhoto") val standPhoto: String? = null,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "bikeCount") val bikeCount: Int? = null,
)

@JsonClass(generateAdapter = true)
data class StandDetailDto(
    @Json(name = "standName") val standName: String,
    @Json(name = "standDescription") val standDescription: String? = null,
    @Json(name = "standPhoto") val standPhoto: String? = null,
    @Json(name = "latitude") val latitude: Double? = null,
    @Json(name = "longitude") val longitude: Double? = null,
    @Json(name = "bikes") val bikes: List<BikeOnStandDto>? = null,
    @Json(name = "note") val note: String? = null,
)

@JsonClass(generateAdapter = true)
data class BikeOnStandDto(
    @Json(name = "bikeNumber") val bikeNumber: Int,
    @Json(name = "note") val note: String? = null,
)
