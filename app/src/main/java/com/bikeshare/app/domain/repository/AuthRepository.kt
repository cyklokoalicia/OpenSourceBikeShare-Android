package com.bikeshare.app.domain.repository

import com.bikeshare.app.util.NetworkResult

interface AuthRepository {
    suspend fun login(number: String, password: String): NetworkResult<Unit>
    suspend fun logout(): NetworkResult<Unit>
    suspend fun isLoggedIn(): Boolean
}
