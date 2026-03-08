package com.bikeshare.app.data.repository

import com.bikeshare.app.data.api.ApiService
import com.bikeshare.app.data.api.dto.LogoutRequest
import com.bikeshare.app.data.api.dto.PhoneConfirmVerifyRequest
import com.bikeshare.app.data.api.dto.RegisterRequest
import com.bikeshare.app.data.api.dto.TokenRequest
import com.bikeshare.app.data.local.TokenStorage
import com.bikeshare.app.domain.repository.AuthRepository
import com.bikeshare.app.util.NetworkResult
import com.bikeshare.app.util.safeApiCall
import com.squareup.moshi.Moshi
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val tokenStorage: TokenStorage,
    private val moshi: Moshi,
) : AuthRepository {

    override suspend fun login(number: String, password: String): NetworkResult<Boolean> {
        val result = safeApiCall(moshi) { api.login(TokenRequest(number, password)) }
        return when (result) {
            is NetworkResult.Success -> {
                val phoneConfirmed = result.data.phoneConfirmed != false
                tokenStorage.saveTokens(
                    result.data.accessToken,
                    result.data.refreshToken,
                    phoneConfirmed,
                )
                NetworkResult.Success(phoneConfirmed)
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun getPhoneConfirmed(): Boolean = tokenStorage.getPhoneConfirmed()

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

    override suspend fun getCities(): NetworkResult<List<String>> =
        safeApiCall(moshi) { api.getCities() }

    override suspend fun register(
        fullname: String,
        city: String,
        useremail: String,
        password: String,
        password2: String,
        number: String,
        agree: Boolean,
    ): NetworkResult<Unit> {
        val result = safeApiCall(moshi) {
            api.register(
                RegisterRequest(
                    fullname = fullname,
                    city = city,
                    useremail = useremail,
                    password = password,
                    password2 = password2,
                    number = number,
                    agree = agree,
                ),
            )
        }
        return when (result) {
            is NetworkResult.Success -> NetworkResult.Success(Unit)
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun requestPhoneConfirm(): NetworkResult<String> {
        val result = safeApiCall(moshi) { api.phoneConfirmRequest() }
        return when (result) {
            is NetworkResult.Success -> NetworkResult.Success(result.data.checkCode)
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun verifyPhoneConfirm(code: String, checkCode: String): NetworkResult<Unit> {
        val result = safeApiCall(moshi) {
            api.phoneConfirmVerify(PhoneConfirmVerifyRequest(code = code, checkCode = checkCode))
        }
        return when (result) {
            is NetworkResult.Success -> NetworkResult.Success(Unit)
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }
}
