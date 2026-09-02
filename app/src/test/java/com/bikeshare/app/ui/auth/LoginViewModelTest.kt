package com.bikeshare.app.ui.auth

import com.bikeshare.app.domain.model.MessengerChat
import com.bikeshare.app.domain.repository.AuthRepository
import com.bikeshare.app.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private class FakeAuthRepository(
        var loggedIn: Boolean = false,
        var loginResult: NetworkResult<Unit> = NetworkResult.Success(Unit),
    ) : AuthRepository {
        override suspend fun isLoggedIn() = loggedIn
        override suspend fun login(number: String, password: String) = loginResult

        override suspend fun logout(): NetworkResult<Unit> = error("not used")
        override suspend fun getCities(): NetworkResult<List<String>> = error("not used")
        override suspend fun getMessengerChats(): NetworkResult<List<MessengerChat>> = error("not used")
        override suspend fun register(
            fullname: String,
            city: String,
            useremail: String,
            password: String,
            password2: String,
            number: String,
            agree: Boolean,
        ): NetworkResult<Unit> = error("not used")

        override suspend fun requestPhoneConfirm(): NetworkResult<String> = error("not used")
        override suspend fun verifyPhoneConfirm(code: String, checkCode: String): NetworkResult<Unit> =
            error("not used")
    }

    // viewModelScope launches on Dispatchers.Main, which does not exist on the JVM —
    // replace it with an eager test dispatcher so coroutines complete synchronously.
    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `no existing session leaves the initial state untouched`() {
        val vm = LoginViewModel(FakeAuthRepository(loggedIn = false))
        assertEquals(LoginUiState(), vm.uiState.value)
    }

    @Test
    fun `existing session goes straight to success`() {
        val vm = LoginViewModel(FakeAuthRepository(loggedIn = true))
        assertTrue(vm.uiState.value.isSuccess)
    }

    @Test
    fun `successful login sets isSuccess`() {
        val vm = LoginViewModel(FakeAuthRepository(loginResult = NetworkResult.Success(Unit)))
        vm.login("0900000000", "secret")
        assertTrue(vm.uiState.value.isSuccess)
        assertNull(vm.uiState.value.error)
    }

    @Test
    fun `failed login surfaces the error message`() {
        val vm = LoginViewModel(FakeAuthRepository(loginResult = NetworkResult.Error("Bad credentials")))
        vm.login("0900000000", "wrong")
        assertEquals("Bad credentials", vm.uiState.value.error)
        assertFalse(vm.uiState.value.isSuccess)
    }

    @Test
    fun `clearError removes the error and keeps the rest of the state`() {
        val vm = LoginViewModel(FakeAuthRepository(loginResult = NetworkResult.Error("Bad credentials")))
        vm.login("0900000000", "wrong")
        vm.clearError()
        assertEquals(LoginUiState(), vm.uiState.value)
    }
}
