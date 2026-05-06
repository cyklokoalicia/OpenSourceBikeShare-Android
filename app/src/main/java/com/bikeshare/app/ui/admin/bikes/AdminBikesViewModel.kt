package com.bikeshare.app.ui.admin.bikes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikeshare.app.data.api.ApiService
import com.bikeshare.app.data.api.dto.BikeDetailDto
import com.bikeshare.app.ui.admin.toggleElement
import com.bikeshare.app.util.NetworkResult
import com.bikeshare.app.util.safeApiCall
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminBikesUiState(
    val bikes: List<BikeDetailDto> = emptyList(),
    val selectedStatuses: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

fun BikeDetailDto.derivedStatus(): String = when {
    userName != null -> "rented"
    !notes.isNullOrBlank() -> "problematic"
    else -> "ok"
}

@HiltViewModel
class AdminBikesViewModel @Inject constructor(
    private val api: ApiService,
    private val moshi: Moshi,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminBikesUiState())
    val uiState: StateFlow<AdminBikesUiState> = _uiState

    init {
        loadBikes()
    }

    fun toggleStatusFilter(status: String) {
        _uiState.update { it.copy(selectedStatuses = it.selectedStatuses.toggleElement(status)) }
    }

    fun loadBikes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = safeApiCall(moshi) { api.getAdminBikes() }) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        bikes = result.data,
                        isLoading = false,
                        error = null,
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message, isLoading = false)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
