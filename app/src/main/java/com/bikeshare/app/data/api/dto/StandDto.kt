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
    @Json(name = "standId") val standId: Int? = null,
    @Json(name = "standName") val standName: String,
    @Json(name = "standDescription") val standDescription: String? = null,
    @Json(name = "standPhoto") val standPhoto: String? = null,
    @Json(name = "serviceTag") val serviceTag: Int? = null,
    @Json(name = "placeName") val placeName: String? = null,
    @Json(name = "latitude") val latitude: Double? = null,
    @Json(name = "longitude") val longitude: Double? = null,
)

/** Wrapper for GET /stands/{standName}/bikes */
@JsonClass(generateAdapter = true)
data class StandBikesResponse(
    @Json(name = "stackTopBike") val stackTopBike: Any? = null,
    @Json(name = "bikesOnStand") val bikesOnStand: List<BikeOnStandDto> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class BikeOnStandDto(
    @Json(name = "bikeNum") val bikeNum: Int,
    @Json(name = "notes") val notes: String? = null,
)
