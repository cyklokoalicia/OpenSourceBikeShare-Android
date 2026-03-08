package com.bikeshare.app.domain.repository

import com.bikeshare.app.data.api.dto.*
import com.bikeshare.app.util.NetworkResult

interface BikeRepository {
    suspend fun getAdminBikes(): NetworkResult<List<BikeDetailDto>>
    suspend fun getAdminBike(bikeNumber: Int): NetworkResult<BikeDetailDto>
    suspend fun getBikeLastUsage(bikeNumber: Int): NetworkResult<BikeLastUsageDto>
    suspend fun getBikeTrip(bikeNumber: Int): NetworkResult<List<BikeTripPointDto>>
    suspend fun setBikeLockCode(bikeNumber: Int, code: String): NetworkResult<Unit>
    suspend fun deleteBikeNotes(bikeNumber: Int): NetworkResult<Unit>
    suspend fun forceRent(bikeNumber: Int): NetworkResult<Unit>
    suspend fun forceReturn(bikeNumber: Int, standName: String, note: String?): NetworkResult<Unit>
    suspend fun revertBike(bikeNumber: Int): NetworkResult<Unit>
}
