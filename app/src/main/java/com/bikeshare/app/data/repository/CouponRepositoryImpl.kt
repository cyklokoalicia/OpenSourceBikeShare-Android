package com.bikeshare.app.data.repository

import com.bikeshare.app.data.api.ApiService
import com.bikeshare.app.data.api.dto.RedeemCouponRequest
import com.bikeshare.app.domain.repository.CouponRepository
import com.bikeshare.app.util.NetworkResult
import com.bikeshare.app.util.safeApiCall
import com.squareup.moshi.Moshi
import javax.inject.Inject

class CouponRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val moshi: Moshi,
) : CouponRepository {

    override suspend fun redeemCoupon(coupon: String): NetworkResult<Unit> {
        val result = safeApiCall(moshi) { api.redeemCoupon(RedeemCouponRequest(coupon)) }
        return when (result) {
            is NetworkResult.Success -> NetworkResult.Success(Unit)
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }
}
