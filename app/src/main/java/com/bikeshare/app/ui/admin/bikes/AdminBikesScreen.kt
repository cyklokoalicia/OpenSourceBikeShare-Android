package com.bikeshare.app.ui.admin.bikes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bikeshare.app.R
import com.bikeshare.app.data.api.dto.BikeDetailDto
import com.bikeshare.app.ui.admin.bikeStatusPalette

private data class BikeStatusOption(
    val value: String,
    val labelRes: Int,
    val icon: ImageVector,
)

private val BikeStatusOptions = listOf(
    BikeStatusOption("problematic", R.string.bike_status_problematic, Icons.Default.Warning),
    BikeStatusOption("rented", R.string.bike_status_rented, Icons.Default.Person),
    BikeStatusOption("ok", R.string.bike_status_ok, Icons.AutoMirrored.Filled.DirectionsBike),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBikesScreen(
    onBack: () -> Unit,
    onBikeClick: (Int) -> Unit,
    viewModel: AdminBikesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    val filteredBikes = remember(uiState.bikes, uiState.selectedStatuses, searchQuery) {
        val byStatus = if (uiState.selectedStatuses.isEmpty()) {
            uiState.bikes
        } else {
            uiState.bikes.filter { it.derivedStatus() in uiState.selectedStatuses }
        }
        if (searchQuery.isBlank()) {
            byStatus
        } else {
            byStatus.filter { bike ->
                bike.bikeNum.toString().contains(searchQuery, ignoreCase = true) ||
                    bike.standName?.contains(searchQuery, ignoreCase = true) == true ||
                    bike.userName?.contains(searchQuery, ignoreCase = true) == true ||
                    bike.notes?.contains(searchQuery, ignoreCase = true) == true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.admin_bikes)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            BikeStatusFilterRow(
                selected = uiState.selectedStatuses,
                onToggle = viewModel::toggleStatusFilter,
            )
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.admin_search_bikes)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                singleLine = true,
            )

            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading && uiState.bikes.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(filteredBikes) { bike ->
                            BikeCard(bike = bike, onClick = { onBikeClick(bike.bikeNum) })
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BikeStatusFilterRow(
    selected: Set<String>,
    onToggle: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BikeStatusOptions.forEach { option ->
            FilterChip(
                selected = option.value in selected,
                onClick = { onToggle(option.value) },
                label = { Text(stringResource(option.labelRes)) },
                leadingIcon = {
                    Icon(option.icon, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize))
                },
            )
        }
    }
}

@Composable
private fun BikeCard(bike: BikeDetailDto, onClick: () -> Unit) {
    val palette = bikeStatusPalette(bike.derivedStatus())
    val containerColor = palette?.container ?: MaterialTheme.colorScheme.surface
    val onColor = palette?.onContainer ?: MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = containerColor, contentColor = onColor),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.AutoMirrored.Filled.DirectionsBike, contentDescription = null, tint = onColor)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("#${bike.bikeNum}", style = MaterialTheme.typography.titleMedium, color = onColor)
                bike.standName?.let {
                    Text("Stand: $it", style = MaterialTheme.typography.bodySmall, color = onColor)
                }
                bike.userName?.let {
                    Text("User: $it", style = MaterialTheme.typography.bodySmall, color = onColor)
                }
                bike.notes?.let {
                    if (it.isNotBlank()) {
                        Text("⚠ $it", style = MaterialTheme.typography.bodySmall, color = onColor)
                    }
                }
            }
        }
    }
}
