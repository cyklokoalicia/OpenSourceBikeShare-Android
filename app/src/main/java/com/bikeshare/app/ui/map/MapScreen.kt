package com.bikeshare.app.ui.map

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bikeshare.app.R
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onScanQr: () -> Unit,
    viewModel: MapViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Default camera position (Bratislava)
    val defaultPosition = LatLng(48.1486, 17.1077)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPosition, 13f)
    }

    // Show snackbar for errors and results
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.rentResult) {
        uiState.rentResult?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearRentResult()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onScanQr,
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = stringResource(R.string.qr_scan))
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = true,
                ),
            ) {
                uiState.stands.forEach { stand ->
                    Marker(
                        state = MarkerState(position = LatLng(stand.latitude, stand.longitude)),
                        title = stand.standName,
                        snippet = "${stand.bikeCount ?: 0} ${stringResource(R.string.bikes_available)}",
                        onClick = {
                            viewModel.selectStand(stand)
                            showBottomSheet = true
                            false
                        },
                    )
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }

        // Stand detail bottom sheet
        if (showBottomSheet && uiState.selectedStand != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                    viewModel.clearSelectedStand()
                },
                sheetState = sheetState,
            ) {
                StandBottomSheet(
                    stand = uiState.selectedStand!!,
                    bikes = uiState.standBikes,
                    onRentBike = { bikeNumber ->
                        viewModel.rentBike(bikeNumber)
                        showBottomSheet = false
                    },
                )
            }
        }
    }
}
