package com.bikeshare.app.ui.admin.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikeshare.app.data.api.ApiService
import com.bikeshare.app.data.api.dto.UserDto
import com.bikeshare.app.util.NetworkResult
import com.bikeshare.app.util.safeApiCall
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUsersUiState(
    val users: List<UserDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class AdminUsersViewModel @Inject constructor(
    private val api: ApiService,
    private val moshi: Moshi,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUsersUiState())
    val uiState: StateFlow<AdminUsersUiState> = _uiState

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = safeApiCall(moshi) { api.getAdminUsers() }) {
                is NetworkResult.Success -> {
                    _uiState.value = AdminUsersUiState(users = result.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = AdminUsersUiState(error = result.message)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
