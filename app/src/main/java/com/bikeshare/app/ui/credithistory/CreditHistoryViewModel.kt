package com.bikeshare.app.ui.credithistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikeshare.app.data.api.dto.CreditHistoryItemDto
import com.bikeshare.app.domain.repository.RentalRepository
import com.bikeshare.app.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreditHistoryUiState(
    val items: List<CreditHistoryItemDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class CreditHistoryViewModel @Inject constructor(
    private val rentalRepository: RentalRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreditHistoryUiState())
    val uiState: StateFlow<CreditHistoryUiState> = _uiState

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = rentalRepository.getCreditHistory()) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        items = result.data,
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
