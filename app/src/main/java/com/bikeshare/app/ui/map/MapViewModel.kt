package com.bikeshare.app.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikeshare.app.data.api.dto.BikeOnStandDto
import com.bikeshare.app.data.api.dto.StandMarkerDto
import com.bikeshare.app.domain.repository.RentalRepository
import com.bikeshare.app.domain.repository.StandRepository
import com.bikeshare.app.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUiState(
    val stands: List<StandMarkerDto> = emptyList(),
    val selectedStand: StandMarkerDto? = null,
    val standBikes: List<BikeOnStandDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val rentResult: String? = null,
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val standRepository: StandRepository,
    private val rentalRepository: RentalRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState

    init {
        loadStandMarkers()
    }

    fun loadStandMarkers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = standRepository.getMarkers()) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        stands = result.data,
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

    fun selectStand(stand: StandMarkerDto) {
        _uiState.value = _uiState.value.copy(selectedStand = stand, standBikes = emptyList())
        loadStandBikes(stand.standName)
    }

    fun clearSelectedStand() {
        _uiState.value = _uiState.value.copy(selectedStand = null, standBikes = emptyList())
    }

    private fun loadStandBikes(standName: String) {
        viewModelScope.launch {
            when (val result = standRepository.getBikes(standName)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(standBikes = result.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun rentBike(bikeNumber: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = rentalRepository.rentBike(bikeNumber)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        rentResult = "Bike $bikeNumber rented successfully!",
                    )
                    // Refresh stand bikes
                    _uiState.value.selectedStand?.let { loadStandBikes(it.standName) }
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message,
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearRentResult() {
        _uiState.value = _uiState.value.copy(rentResult = null)
    }
}
