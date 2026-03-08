package com.bikeshare.app.domain.repository

import com.bikeshare.app.data.api.dto.BikeOnStandDto
import com.bikeshare.app.data.api.dto.StandMarkerDto
import com.bikeshare.app.util.NetworkResult

interface StandRepository {
    suspend fun getMarkers(): NetworkResult<List<StandMarkerDto>>
    suspend fun getBikes(standName: String): NetworkResult<List<BikeOnStandDto>>
}
