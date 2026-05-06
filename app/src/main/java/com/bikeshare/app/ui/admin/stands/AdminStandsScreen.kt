package com.bikeshare.app.ui.admin.stands

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.VisibilityOff
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
import com.bikeshare.app.data.api.dto.StandDetailDto
import com.bikeshare.app.ui.admin.standStatusPalette

private data class StandStatusOption(
    val value: String,
    val labelRes: Int,
    val icon: ImageVector?,
)

private val StandStatusOptions = listOf(
    StandStatusOption("active", R.string.stand_status_active, null),
    StandStatusOption("technical", R.string.stand_status_technical, Icons.Default.Build),
    StandStatusOption("hidden", R.string.stand_status_hidden, Icons.Default.VisibilityOff),
    StandStatusOption("inactive", R.string.stand_status_inactive, Icons.Default.Block),
    StandStatusOption("virtual", R.string.stand_status_virtual, Icons.Default.Public),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStandsScreen(
    onBack: () -> Unit,
    viewModel: AdminStandsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.admin_stands)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            StandStatusFilterRow(
                selected = uiState.selectedStatuses,
                onToggle = viewModel::toggleStatusFilter,
            )
            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading && uiState.stands.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(uiState.visibleStands) { stand ->
                            StandCard(stand = stand)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StandStatusFilterRow(
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
        StandStatusOptions.forEach { option ->
            FilterChip(
                selected = option.value in selected,
                onClick = { onToggle(option.value) },
                label = { Text(stringResource(option.labelRes)) },
                leadingIcon = option.icon?.let {
                    { Icon(it, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                },
            )
        }
    }
}

@Composable
private fun StandCard(stand: StandDetailDto) {
    val palette = standStatusPalette(stand.status)
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette?.container ?: MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val onColor = palette?.onContainer ?: MaterialTheme.colorScheme.onSurfaceVariant
                Icon(Icons.Default.Place, contentDescription = null, tint = onColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stand.standName,
                    style = MaterialTheme.typography.titleMedium,
                    color = onColor,
                    modifier = Modifier.weight(1f),
                )
                stand.status?.let {
                    Text(
                        text = it.replaceFirstChar(Char::uppercase),
                        style = MaterialTheme.typography.labelSmall,
                        color = onColor,
                    )
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                stand.standDescription?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                stand.placeName?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
