package com.bikeshare.app.ui.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikeshare.app.data.api.dto.TripItemDto
import com.bikeshare.app.domain.repository.RentalRepository
import com.bikeshare.app.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TripsUiState(
    val trips: List<TripItemDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val rentalRepository: RentalRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripsUiState())
    val uiState: StateFlow<TripsUiState> = _uiState

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = rentalRepository.getMyTrips()) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        trips = result.data,
                        isLoading = false,
                    )
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
}
