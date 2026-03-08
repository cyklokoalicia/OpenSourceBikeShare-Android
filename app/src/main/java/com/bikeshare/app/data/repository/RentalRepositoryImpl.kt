package com.bikeshare.app.data.repository

import com.bikeshare.app.data.api.ApiService
import com.bikeshare.app.data.api.dto.RentRequest
import com.bikeshare.app.data.api.dto.RentedBikeDto
import com.bikeshare.app.data.api.dto.ReturnRequest
import com.bikeshare.app.data.api.dto.UserLimitsDto
import com.bikeshare.app.domain.repository.RentalRepository
import com.bikeshare.app.util.NetworkResult
import com.bikeshare.app.util.safeApiCall
import com.squareup.moshi.Moshi
import javax.inject.Inject

class RentalRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val moshi: Moshi,
) : RentalRepository {

    override suspend fun rentBike(bikeNumber: Int): NetworkResult<Unit> {
        val result = safeApiCall(moshi) { api.rentBike(RentRequest(bikeNumber)) }
        return when (result) {
            is NetworkResult.Success -> NetworkResult.Success(Unit)
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun returnBike(
        bikeNumber: Int,
        standName: String,
        note: String?,
    ): NetworkResult<Unit> {
        val result = safeApiCall(moshi) {
            api.returnBike(ReturnRequest(bikeNumber, standName, note))
        }
        return when (result) {
            is NetworkResult.Success -> NetworkResult.Success(Unit)
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun getMyBikes(): NetworkResult<List<RentedBikeDto>> {
        return safeApiCall(moshi) { api.getMyBikes() }
    }

    override suspend fun getMyLimits(): NetworkResult<UserLimitsDto> {
        return safeApiCall(moshi) { api.getMyLimits() }
    }
}
