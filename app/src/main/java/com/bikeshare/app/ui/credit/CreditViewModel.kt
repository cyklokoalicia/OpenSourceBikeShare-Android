package com.bikeshare.app.ui.credit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikeshare.app.domain.repository.CouponRepository
import com.bikeshare.app.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreditUiState(
    val isLoading: Boolean = false,
    val success: String? = null,
    val error: String? = null,
)

@HiltViewModel
class CreditViewModel @Inject constructor(
    private val couponRepository: CouponRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreditUiState())
    val uiState: StateFlow<CreditUiState> = _uiState

    fun redeemCoupon(coupon: String) {
        viewModelScope.launch {
            _uiState.value = CreditUiState(isLoading = true)
            when (val result = couponRepository.redeemCoupon(coupon)) {
                is NetworkResult.Success -> {
                    _uiState.value = CreditUiState(success = "Coupon redeemed successfully!")
                }
                is NetworkResult.Error -> {
                    _uiState.value = CreditUiState(error = result.message)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
