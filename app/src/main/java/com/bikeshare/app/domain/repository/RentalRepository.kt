package com.bikeshare.app.domain.repository

import com.bikeshare.app.data.api.dto.RentedBikeDto
import com.bikeshare.app.data.api.dto.UserLimitsDto
import com.bikeshare.app.util.NetworkResult

interface RentalRepository {
    suspend fun rentBike(bikeNumber: Int): NetworkResult<Unit>
    suspend fun returnBike(bikeNumber: Int, standName: String, note: String?): NetworkResult<Unit>
    suspend fun getMyBikes(): NetworkResult<List<RentedBikeDto>>
    suspend fun getMyLimits(): NetworkResult<UserLimitsDto>
}
