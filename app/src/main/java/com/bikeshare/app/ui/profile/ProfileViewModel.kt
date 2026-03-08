package com.bikeshare.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikeshare.app.data.api.dto.UserLimitsDto
import com.bikeshare.app.domain.repository.AuthRepository
import com.bikeshare.app.domain.repository.RentalRepository
import com.bikeshare.app.domain.repository.UserRepository
import com.bikeshare.app.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val limits: UserLimitsDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val loggedOut: Boolean = false,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val rentalRepository: RentalRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadLimits()
    }

    fun loadLimits() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = rentalRepository.getMyLimits()) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        limits = result.data,
                        isLoading = false,
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        error = result.message,
                        isLoading = false,
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun changeCity(city: String) {
        viewModelScope.launch {
            when (val result = userRepository.changeCity(city)) {
                is NetworkResult.Success -> loadLimits()
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = _uiState.value.copy(loggedOut = true)
        }
    }
}
