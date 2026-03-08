package com.bikeshare.app.ui.admin.coupons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikeshare.app.data.api.ApiService
import com.bikeshare.app.data.api.dto.CouponDto
import com.bikeshare.app.data.api.dto.GenerateCouponsRequest
import com.bikeshare.app.util.NetworkResult
import com.bikeshare.app.util.safeApiCall
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminCouponsUiState(
    val coupons: List<CouponDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
)

@HiltViewModel
class AdminCouponsViewModel @Inject constructor(
    private val api: ApiService,
    private val moshi: Moshi,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminCouponsUiState())
    val uiState: StateFlow<AdminCouponsUiState> = _uiState

    init {
        loadCoupons()
    }

    fun loadCoupons() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = safeApiCall(moshi) { api.getAdminCoupons() }) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(coupons = result.data, isLoading = false)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message, isLoading = false)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun generateCoupons(multiplier: Int) {
        viewModelScope.launch {
            when (val result = safeApiCall(moshi) { api.generateCoupons(GenerateCouponsRequest(multiplier)) }) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(message = "Coupons generated")
                    loadCoupons()
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun sellCoupon(coupon: String) {
        viewModelScope.launch {
            when (val result = safeApiCall(moshi) { api.sellCoupon(coupon) }) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(message = "Coupon $coupon marked as sold")
                    loadCoupons()
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
