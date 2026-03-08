package com.bikeshare.app.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CouponDto(
    @Json(name = "coupon") val coupon: String,
    @Json(name = "value") val value: Double? = null,
    @Json(name = "status") val status: String? = null,
)

@JsonClass(generateAdapter = true)
data class RedeemCouponRequest(
    @Json(name = "coupon") val coupon: String,
)

@JsonClass(generateAdapter = true)
data class GenerateCouponsRequest(
    @Json(name = "multiplier") val multiplier: Int,
)
