package com.bikeshare.app.ui.admin.users

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bikeshare.app.R
import com.bikeshare.app.data.api.ApiService
import com.bikeshare.app.data.api.dto.AddCreditRequest
import com.bikeshare.app.data.api.dto.UserDto
import com.bikeshare.app.util.NetworkResult
import com.bikeshare.app.util.safeApiCall
import com.squareup.moshi.Moshi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUserEditUiState(
    val user: UserDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
)

@HiltViewModel
class AdminUserEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val api: ApiService,
    private val moshi: Moshi,
) : ViewModel() {

    private val userId: Int = savedStateHandle["userId"] ?: 0

    private val _uiState = MutableStateFlow(AdminUserEditUiState())
    val uiState: StateFlow<AdminUserEditUiState> = _uiState

    init {
        loadUser()
    }

    fun loadUser() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = safeApiCall(moshi) { api.getAdminUser(userId) }) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(user = result.data, isLoading = false)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message, isLoading = false)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun addCredit(multiplier: Int) {
        viewModelScope.launch {
            when (val result = safeApiCall(moshi) { api.addUserCredit(userId, AddCreditRequest(multiplier)) }) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(message = "Credit added (x$multiplier)")
                    loadUser()
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun updateUser(fields: Map<String, Any>) {
        viewModelScope.launch {
            when (val result = safeApiCall(moshi) { api.updateUser(userId, fields) }) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(message = "User updated")
                    loadUser()
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
fun AdminUserEditScreen(
    onBack: () -> Unit,
    viewModel: AdminUserEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var creditMultiplier by remember { mutableStateOf("1") }

    LaunchedEffect(uiState.message) { uiState.message?.let { snackbarHostState.showSnackbar(it) } }
    LaunchedEffect(uiState.error) { uiState.error?.let { snackbarHostState.showSnackbar(it) } }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(uiState.user?.userName ?: "User") },
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

            uiState.user?.let { user ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("User #${user.userId}", style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        user.userName?.let { Text("Name: $it") }
                        user.phone?.let { Text("Phone: $it") }
                        user.email?.let { Text("Email: $it") }
                        user.city?.let { Text("City: $it") }
                        Text("Privileges: ${user.privileges ?: 0}")
                        user.credit?.let { Text("Credit: $it") }
                        user.limit?.let { Text("Limit: $it") }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Add Credit", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = creditMultiplier,
                        onValueChange = { if (it.all { c -> c.isDigit() }) creditMultiplier = it },
                        label = { Text("Multiplier") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { creditMultiplier.toIntOrNull()?.let { viewModel.addCredit(it) } },
                        enabled = (creditMultiplier.toIntOrNull() ?: 0) > 0,
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}
