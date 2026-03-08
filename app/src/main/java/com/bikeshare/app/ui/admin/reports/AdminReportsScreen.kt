package com.bikeshare.app.ui.admin.reports

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Card(
                onClick = { viewModel.loadDailyReport() },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(modifier = Modifier.padding(20.dp)) {
                    Icon(Icons.Default.Today, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Daily Report", style = MaterialTheme.typography.titleMedium)
                }
            }

            Card(
                onClick = { viewModel.loadUserReport() },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(modifier = Modifier.padding(20.dp)) {
                    Icon(Icons.Default.People, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("User Report (Current Year)", style = MaterialTheme.typography.titleMedium)
                }
            }

            Card(
                onClick = { viewModel.loadInactiveBikes() },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(modifier = Modifier.padding(20.dp)) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Inactive Bikes", style = MaterialTheme.typography.titleMedium)
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator()
            }

            // Display report data
            uiState.dailyReport?.let {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Daily Report", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it.toString(), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            uiState.userReport?.let {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("User Report", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it.toString(), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            uiState.inactiveBikes?.let {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Inactive Bikes", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it.toString(), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            uiState.error?.let {
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
