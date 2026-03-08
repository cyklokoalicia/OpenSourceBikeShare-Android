package com.bikeshare.app.data.repository

import com.bikeshare.app.data.api.ApiService
import com.bikeshare.app.data.api.dto.LogoutRequest
import com.bikeshare.app.data.api.dto.TokenRequest
import com.bikeshare.app.data.local.TokenStorage
import com.bikeshare.app.domain.repository.AuthRepository
import com.bikeshare.app.util.NetworkResult
import com.squareup.moshi.Moshi
import com.bikeshare.app.util.safeApiCall
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val tokenStorage: TokenStorage,
    private val moshi: Moshi,
) : AuthRepository {

    override suspend fun login(number: String, password: String): NetworkResult<Unit> {
        val result = safeApiCall(moshi) { api.login(TokenRequest(number, password)) }
        return when (result) {
            is NetworkResult.Success -> {
                tokenStorage.saveTokens(result.data.accessToken, result.data.refreshToken)
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun logout(): NetworkResult<Unit> {
        val refreshToken = tokenStorage.getRefreshToken()
        val result = safeApiCall(moshi) { api.logout(LogoutRequest(refreshToken)) }
        tokenStorage.clearTokens()
        return when (result) {
            is NetworkResult.Success -> NetworkResult.Success(Unit)
            is NetworkResult.Error -> NetworkResult.Success(Unit) // still clear tokens locally
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun isLoggedIn(): Boolean = tokenStorage.hasTokens()
}
