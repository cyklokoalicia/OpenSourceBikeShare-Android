package com.bikeshare.app.ui.admin.stands

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikeshare.app.data.api.ApiService
import com.bikeshare.app.data.api.dto.StandDetailDto
import com.bikeshare.app.util.NetworkResult
import com.bikeshare.app.util.safeApiCall
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminStandsUiState(
    val stands: List<StandDetailDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
)

@HiltViewModel
class AdminStandsViewModel @Inject constructor(
    private val api: ApiService,
    private val moshi: Moshi,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminStandsUiState())
    val uiState: StateFlow<AdminStandsUiState> = _uiState

    init {
        loadStands()
    }

    fun loadStands() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = safeApiCall(moshi) { api.getAdminStands() }) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(stands = result.data, isLoading = false)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message, isLoading = false)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun deleteStandNotes(standName: String) {
        viewModelScope.launch {
            when (val result = safeApiCall(moshi) { api.deleteStandNotes(standName) }) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(message = "Notes deleted for $standName")
                    loadStands()
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
