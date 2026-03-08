package com.bikeshare.app.data.repository

import com.bikeshare.app.data.api.ApiService
import com.bikeshare.app.data.api.dto.ChangeCityRequest
import com.bikeshare.app.domain.repository.UserRepository
import com.bikeshare.app.util.NetworkResult
import com.bikeshare.app.util.safeApiCall
import com.squareup.moshi.Moshi
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val moshi: Moshi,
) : UserRepository {

    override suspend fun changeCity(city: String): NetworkResult<Unit> {
        val result = safeApiCall(moshi) { api.changeCity(ChangeCityRequest(city)) }
        return when (result) {
            is NetworkResult.Success -> NetworkResult.Success(Unit)
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }
}
