package com.bikeshare.app.ui.admin.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikeshare.app.data.api.ApiService
import com.bikeshare.app.data.api.dto.DailyReportDto
import com.bikeshare.app.data.api.dto.InactiveBikeDto
import com.bikeshare.app.data.api.dto.UserReportDto
import com.bikeshare.app.util.NetworkResult
import com.bikeshare.app.util.safeApiCall
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminReportsUiState(
    val dailyReport: List<DailyReportDto>? = null,
    val userReport: List<UserReportDto>? = null,
    val inactiveBikes: List<InactiveBikeDto>? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class AdminReportsViewModel @Inject constructor(
    private val api: ApiService,
    private val moshi: Moshi,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminReportsUiState())
    val uiState: StateFlow<AdminReportsUiState> = _uiState

    fun loadDailyReport() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, userReport = null, inactiveBikes = null)
            when (val result = safeApiCall(moshi) { api.getDailyReport() }) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(dailyReport = result.data, isLoading = false)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message, isLoading = false)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun loadUserReport(year: Int? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, dailyReport = null, inactiveBikes = null)
            val result = if (year != null) {
                safeApiCall(moshi) { api.getUserReportByYear(year) }
            } else {
                safeApiCall(moshi) { api.getUserReport() }
            }
            when (result) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(userReport = result.data, isLoading = false)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message, isLoading = false)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun loadInactiveBikes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, dailyReport = null, userReport = null)
            when (val result = safeApiCall(moshi) { api.getInactiveBikesReport() }) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(inactiveBikes = result.data, isLoading = false)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message, isLoading = false)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
