package com.bikeshare.app.data.repository

import com.bikeshare.app.data.api.ApiService
import com.bikeshare.app.data.api.dto.*
import com.bikeshare.app.domain.repository.BikeRepository
import com.bikeshare.app.util.NetworkResult
import com.bikeshare.app.util.safeApiCall
import com.squareup.moshi.Moshi
import javax.inject.Inject

class BikeRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val moshi: Moshi,
) : BikeRepository {

    override suspend fun getAdminBikes(): NetworkResult<List<BikeDetailDto>> =
        safeApiCall(moshi) { api.getAdminBikes() }

    override suspend fun getAdminBike(bikeNumber: Int): NetworkResult<BikeDetailDto> =
        safeApiCall(moshi) { api.getAdminBike(bikeNumber) }

    override suspend fun getBikeLastUsage(bikeNumber: Int): NetworkResult<BikeLastUsageDto> =
        safeApiCall(moshi) { api.getBikeLastUsage(bikeNumber) }

    override suspend fun getBikeTrip(bikeNumber: Int): NetworkResult<List<BikeTripPointDto>> =
        safeApiCall(moshi) { api.getBikeTrip(bikeNumber) }

    override suspend fun setBikeLockCode(bikeNumber: Int, code: String): NetworkResult<Unit> {
        val result = safeApiCall(moshi) { api.setBikeLockCode(bikeNumber, SetLockCodeRequest(code)) }
        return when (result) {
            is NetworkResult.Success -> NetworkResult.Success(Unit)
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun deleteBikeNotes(bikeNumber: Int): NetworkResult<Unit> {
        val result = safeApiCall(moshi) { api.deleteBikeNotes(bikeNumber) }
        return when (result) {
            is NetworkResult.Success -> NetworkResult.Success(Unit)
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun forceRent(bikeNumber: Int): NetworkResult<Unit> {
        val result = safeApiCall(moshi) { api.forceRent(ForceRentRequest(bikeNumber)) }
        return when (result) {
            is NetworkResult.Success -> NetworkResult.Success(Unit)
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun forceReturn(
        bikeNumber: Int,
        standName: String,
        note: String?,
    ): NetworkResult<Unit> {
        val result = safeApiCall(moshi) {
            api.forceReturn(ForceReturnRequest(bikeNumber, standName, note))
        }
        return when (result) {
            is NetworkResult.Success -> NetworkResult.Success(Unit)
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun revertBike(bikeNumber: Int): NetworkResult<Unit> {
        val result = safeApiCall(moshi) { api.revertBike(RevertRequest(bikeNumber)) }
        return when (result) {
            is NetworkResult.Success -> NetworkResult.Success(Unit)
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }
}
