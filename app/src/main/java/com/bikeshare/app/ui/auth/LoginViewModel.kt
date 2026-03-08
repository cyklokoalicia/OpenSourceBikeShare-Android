package com.bikeshare.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikeshare.app.domain.repository.AuthRepository
import com.bikeshare.app.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val phoneConfirmed: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    init {
        checkExistingSession()
    }

    private fun checkExistingSession() {
        viewModelScope.launch {
            if (authRepository.isLoggedIn()) {
                val phoneConfirmed = authRepository.getPhoneConfirmed()
                _uiState.value = LoginUiState(isSuccess = true, phoneConfirmed = phoneConfirmed)
            }
        }
    }

    fun login(number: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            when (val result = authRepository.login(number, password)) {
                is NetworkResult.Success -> {
                    _uiState.value = LoginUiState(
                        isSuccess = true,
                        phoneConfirmed = result.data,
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = LoginUiState(error = result.message)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
