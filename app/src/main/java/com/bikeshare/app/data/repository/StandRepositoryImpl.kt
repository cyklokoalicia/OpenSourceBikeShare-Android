package com.bikeshare.app.data.repository

import com.bikeshare.app.data.api.ApiService
import com.bikeshare.app.data.api.dto.BikeOnStandDto
import com.bikeshare.app.data.api.dto.StandMarkerDto
import com.bikeshare.app.domain.repository.StandRepository
import com.bikeshare.app.util.NetworkResult
import com.bikeshare.app.util.safeApiCall
import com.squareup.moshi.Moshi
import javax.inject.Inject

class StandRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val moshi: Moshi,
) : StandRepository {

    override suspend fun getMarkers(): NetworkResult<List<StandMarkerDto>> {
        return safeApiCall(moshi) { api.getStandMarkers() }
    }

    override suspend fun getBikes(standName: String): NetworkResult<List<BikeOnStandDto>> {
        return when (val result = safeApiCall(moshi) { api.getStandBikes(standName) }) {
            is NetworkResult.Success -> NetworkResult.Success(result.data.bikesOnStand)
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }
}
