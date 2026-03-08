package com.bikeshare.app.ui.scanner

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class QrScannerUiState(
    val scannedValue: String? = null,
    val isProcessing: Boolean = false,
)

@HiltViewModel
class QrScannerViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(QrScannerUiState())
    val uiState: StateFlow<QrScannerUiState> = _uiState

    fun onQrScanned(value: String) {
        _uiState.value = QrScannerUiState(scannedValue = value, isProcessing = true)
        // Parse QR code: rent/{bikeNumber} or return/{standName}
    }
}
