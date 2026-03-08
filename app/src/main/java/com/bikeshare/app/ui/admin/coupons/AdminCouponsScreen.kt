package com.bikeshare.app.ui.admin.coupons

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bikeshare.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCouponsScreen(
    onBack: () -> Unit,
    viewModel: AdminCouponsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showGenerateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.message) { uiState.message?.let { snackbarHostState.showSnackbar(it) } }
    LaunchedEffect(uiState.error) { uiState.error?.let { snackbarHostState.showSnackbar(it) } }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.admin_coupons)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { showGenerateDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Generate")
                    }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (uiState.isLoading && uiState.coupons.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(uiState.coupons) { coupon ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column {
                                    Text(coupon.coupon, style = MaterialTheme.typography.titleMedium)
                                    coupon.value?.let { Text("Value: $it", style = MaterialTheme.typography.bodySmall) }
                                    coupon.status?.let { Text("Status: $it", style = MaterialTheme.typography.bodySmall) }
                                }
                                IconButton(onClick = { viewModel.sellCoupon(coupon.coupon) }) {
                                    Icon(Icons.Default.Sell, contentDescription = "Mark as sold")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showGenerateDialog) {
        var multiplier by remember { mutableStateOf("1") }
        AlertDialog(
            onDismissRequest = { showGenerateDialog = false },
            title = { Text("Generate Coupons") },
            text = {
                OutlinedTextField(
                    value = multiplier,
                    onValueChange = { if (it.all { c -> c.isDigit() }) multiplier = it },
                    label = { Text("Multiplier") },
                    singleLine = true,
                )
            },
            confirmButton = {
                Button(onClick = {
                    multiplier.toIntOrNull()?.let { viewModel.generateCoupons(it) }
                    showGenerateDialog = false
                }) { Text("Generate") }
            },
            dismissButton = {
                TextButton(onClick = { showGenerateDialog = false }) { Text(stringResource(R.string.cancel)) }
            },
        )
    }
}
