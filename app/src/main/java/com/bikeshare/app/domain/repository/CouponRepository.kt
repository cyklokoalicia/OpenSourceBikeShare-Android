package com.bikeshare.app.domain.repository

import com.bikeshare.app.util.NetworkResult

interface CouponRepository {
    suspend fun redeemCoupon(coupon: String): NetworkResult<Unit>
}
