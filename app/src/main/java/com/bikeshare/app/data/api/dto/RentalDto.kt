package com.bikeshare.app.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RentRequest(
    @Json(name = "bikeNumber") val bikeNumber: Int,
)

@JsonClass(generateAdapter = true)
data class ReturnRequest(
    @Json(name = "bikeNumber") val bikeNumber: Int,
    @Json(name = "standName") val standName: String,
    @Json(name = "note") val note: String? = null,
)

@JsonClass(generateAdapter = true)
data class RentedBikeDto(
    @Json(name = "bikeNumber") val bikeNumber: Int,
    @Json(name = "lockCode") val lockCode: String? = null,
    @Json(name = "standName") val standName: String? = null,
    @Json(name = "rentedAt") val rentedAt: String? = null,
)

@JsonClass(generateAdapter = true)
data class RevertRequest(
    @Json(name = "bikeNumber") val bikeNumber: Int,
)

@JsonClass(generateAdapter = true)
data class ForceRentRequest(
    @Json(name = "bikeNumber") val bikeNumber: Int,
)

@JsonClass(generateAdapter = true)
data class ForceReturnRequest(
    @Json(name = "bikeNumber") val bikeNumber: Int,
    @Json(name = "standName") val standName: String,
    @Json(name = "note") val note: String? = null,
)
