package com.bikeshare.app.domain.repository

import com.bikeshare.app.util.NetworkResult

interface AuthRepository {
    suspend fun login(number: String, password: String): NetworkResult<Boolean>

    suspend fun logout(): NetworkResult<Unit>
    suspend fun isLoggedIn(): Boolean
    suspend fun getPhoneConfirmed(): Boolean
    suspend fun getCities(): NetworkResult<List<String>>
    suspend fun register(
        fullname: String,
        city: String,
        useremail: String,
        password: String,
        password2: String,
        number: String,
        agree: Boolean,
    ): NetworkResult<Unit>

    suspend fun requestPhoneConfirm(): NetworkResult<String>

    suspend fun verifyPhoneConfirm(code: String, checkCode: String): NetworkResult<Unit>
}
