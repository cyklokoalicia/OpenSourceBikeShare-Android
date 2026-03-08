package com.bikeshare.app.domain.repository

import com.bikeshare.app.data.api.dto.CreditHistoryItemDto
import com.bikeshare.app.data.api.dto.RentedBikeDto
import com.bikeshare.app.data.api.dto.RentSystemResultDto
import com.bikeshare.app.data.api.dto.TripItemDto
import com.bikeshare.app.data.api.dto.UserLimitsDto
import com.bikeshare.app.util.NetworkResult

interface RentalRepository {
    suspend fun rentBike(bikeNumber: Int): NetworkResult<RentSystemResultDto>
    suspend fun returnBike(bikeNumber: Int, standName: String, note: String?): NetworkResult<RentSystemResultDto>
    suspend fun getMyBikes(): NetworkResult<List<RentedBikeDto>>
    suspend fun getMyLimits(): NetworkResult<UserLimitsDto>
    suspend fun getCreditHistory(): NetworkResult<List<CreditHistoryItemDto>>
    suspend fun getMyTrips(): NetworkResult<List<TripItemDto>>
}
