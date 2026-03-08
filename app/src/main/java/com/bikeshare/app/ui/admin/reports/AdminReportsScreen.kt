package com.bikeshare.app.ui.admin.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bikeshare.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReportsScreen(
    onBack: () -> Unit,
    viewModel: AdminReportsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.admin_reports)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Action buttons
            item {
                Card(onClick = { viewModel.loadDailyReport() }, modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(20.dp)) {
                        Icon(Icons.Default.Today, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Daily Report", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            item {
                Card(onClick = { viewModel.loadUserReport() }, modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(20.dp)) {
                        Icon(Icons.Default.People, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("User Report (Current Year)", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            item {
                Card(onClick = { viewModel.loadInactiveBikes() }, modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(20.dp)) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Inactive Bikes", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            if (uiState.isLoading) {
                item { CircularProgressIndicator() }
            }

            // Daily report table
            uiState.dailyReport?.let { report ->
                item {
                    Text("Daily Report", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                        Text("Date", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                        Text("Rents", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                        Text("Returns", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                    }
                }
                items(report) { day ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                            Text(day.day, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                            Text("${day.rentCount}", modifier = Modifier.weight(0.5f), style = MaterialTheme.typography.bodyMedium)
                            Text("${day.returnCount}", modifier = Modifier.weight(0.5f), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // User report table
            uiState.userReport?.let { report ->
                item {
                    Text("User Report", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                        Text("User", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                        Text("Rents", modifier = Modifier.weight(0.4f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                        Text("Returns", modifier = Modifier.weight(0.4f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                        Text("Total", modifier = Modifier.weight(0.4f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                    }
                }
                items(report) { user ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                            Text(user.username, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                            Text("${user.rentCount}", modifier = Modifier.weight(0.4f), style = MaterialTheme.typography.bodyMedium)
                            Text("${user.returnCount}", modifier = Modifier.weight(0.4f), style = MaterialTheme.typography.bodyMedium)
                            Text("${user.totalActionCount}", modifier = Modifier.weight(0.4f), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Inactive bikes table
            uiState.inactiveBikes?.let { bikes ->
                item {
                    Text("Inactive Bikes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                        Text("Bike", modifier = Modifier.weight(0.4f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                        Text("Stand", modifier = Modifier.weight(0.6f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                        Text("Days", modifier = Modifier.weight(0.3f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                    }
                }
                items(bikes) { bike ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                            Text("#${bike.bikeNum}", modifier = Modifier.weight(0.4f), style = MaterialTheme.typography.bodyMedium)
                            Text(bike.standName, modifier = Modifier.weight(0.6f), style = MaterialTheme.typography.bodyMedium)
                            Text("${bike.inactiveDays}d", modifier = Modifier.weight(0.3f), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            uiState.error?.let {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    ) {
                        Text(it, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }
        }
    }
}
