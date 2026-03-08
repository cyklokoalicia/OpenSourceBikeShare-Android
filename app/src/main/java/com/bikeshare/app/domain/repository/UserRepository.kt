package com.bikeshare.app.domain.repository

import com.bikeshare.app.util.NetworkResult

interface UserRepository {
    suspend fun changeCity(city: String): NetworkResult<Unit>
}
