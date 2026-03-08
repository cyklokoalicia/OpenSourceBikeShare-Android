package com.bikeshare.app.ui.admin.bikes

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bikeshare.app.R
import com.bikeshare.app.data.api.dto.BikeDetailDto
import com.bikeshare.app.domain.repository.BikeRepository
import com.bikeshare.app.util.NetworkResult
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.bikeshare.app.data.api.ApiService
import com.bikeshare.app.util.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminBikeDetailUiState(
    val bike: BikeDetailDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
)

@HiltViewModel
class AdminBikeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val api: ApiService,
    private val moshi: Moshi,
    private val bikeRepository: BikeRepository,
) : ViewModel() {

    private val bikeNumber: Int = savedStateHandle["bikeNumber"] ?: 0

    private val _uiState = MutableStateFlow(AdminBikeDetailUiState())
    val uiState: StateFlow<AdminBikeDetailUiState> = _uiState

    init {
        loadBike()
    }

    fun loadBike() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = safeApiCall(moshi) { api.getAdminBike(bikeNumber) }) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(bike = result.data, isLoading = false)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message, isLoading = false)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun setLockCode(code: String) {
        viewModelScope.launch {
            when (val result = bikeRepository.setBikeLockCode(bikeNumber, code)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(message = "Lock code updated")
                    loadBike()
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun deleteNotes() {
        viewModelScope.launch {
            when (val result = bikeRepository.deleteBikeNotes(bikeNumber)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(message = "Notes deleted")
                    loadBike()
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun revertBike() {
        viewModelScope.launch {
            when (val result = bikeRepository.revertBike(bikeNumber)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(message = "Bike state reverted")
                    loadBike()
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBikeDetailScreen(
    onBack: () -> Unit,
    viewModel: AdminBikeDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showLockCodeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { snackbarHostState.showSnackbar(it) }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Bike #${uiState.bike?.bikeNumber ?: ""}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            uiState.bike?.let { bike ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Bike #${bike.bikeNumber}", style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        bike.currentStand?.let { Text("Stand: $it") }
                        bike.currentUser?.let { Text("Rented by: $it", color = MaterialTheme.colorScheme.primary) }
                        bike.note?.let { Text("Note: $it", color = MaterialTheme.colorScheme.error) }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(
                        onClick = { showLockCodeDialog = true },
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Set Code")
                    }

                    OutlinedButton(
                        onClick = { viewModel.deleteNotes() },
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Del Notes")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.revertBike() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                ) {
                    Icon(Icons.Default.Undo, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Revert Bike State")
                }
            }
        }
    }

    if (showLockCodeDialog) {
        var code by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showLockCodeDialog = false },
            title = { Text("Set Lock Code") },
            text = {
                OutlinedTextField(
                    value = code,
                    onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) code = it },
                    label = { Text("4-digit code") },
                    singleLine = true,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setLockCode(code)
                        showLockCodeDialog = false
                    },
                    enabled = code.length == 4,
                ) { Text("Set") }
            },
            dismissButton = {
                TextButton(onClick = { showLockCodeDialog = false }) { Text(stringResource(R.string.cancel)) }
            },
        )
    }
}
