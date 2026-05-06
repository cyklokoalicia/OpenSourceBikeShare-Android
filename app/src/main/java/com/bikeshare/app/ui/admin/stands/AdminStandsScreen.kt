package com.bikeshare.app.ui.admin.stands

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bikeshare.app.R
import com.bikeshare.app.data.api.dto.StandDetailDto
import com.bikeshare.app.ui.admin.StandStatusOptions
import com.bikeshare.app.ui.admin.StatusFilterRow
import com.bikeshare.app.ui.admin.standStatusOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStandsScreen(
    onBack: () -> Unit,
    viewModel: AdminStandsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val visibleStands = remember(uiState.stands, uiState.selectedStatuses) {
        if (uiState.selectedStatuses.isEmpty()) {
            uiState.stands
        } else {
            uiState.stands.filter { (it.status ?: "active") in uiState.selectedStatuses }
        }
    }

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
            StatusFilterRow(
                options = StandStatusOptions,
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
                        items(visibleStands) { stand ->
                            StandCard(stand = stand)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StandCard(stand: StandDetailDto) {
    val option = standStatusOption(stand.status)
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(option?.palette?.container ?: MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val onColor = option?.palette?.onContainer ?: MaterialTheme.colorScheme.onSurfaceVariant
                Icon(Icons.Default.Place, contentDescription = null, tint = onColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stand.standName,
                    style = MaterialTheme.typography.titleMedium,
                    color = onColor,
                    modifier = Modifier.weight(1f),
                )
                option?.let {
                    Text(
                        text = stringResource(it.labelRes),
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
