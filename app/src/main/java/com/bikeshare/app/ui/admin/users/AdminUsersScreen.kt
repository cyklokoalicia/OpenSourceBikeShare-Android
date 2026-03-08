package com.bikeshare.app.ui.admin.users

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
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
fun AdminUsersScreen(
    onBack: () -> Unit,
    onUserClick: (Int) -> Unit,
    viewModel: AdminUsersViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    val filteredUsers = remember(uiState.users, searchQuery) {
        if (searchQuery.isBlank()) uiState.users
        else uiState.users.filter { user ->
            user.username?.contains(searchQuery, ignoreCase = true) == true ||
                user.number?.contains(searchQuery, ignoreCase = true) == true ||
                user.mail?.contains(searchQuery, ignoreCase = true) == true ||
                user.userId.toString().contains(searchQuery)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.admin_users)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.admin_search_users)) },
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
                if (uiState.isLoading && uiState.users.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(filteredUsers) { user ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onUserClick(user.userId) },
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(user.username ?: "User #${user.userId}", style = MaterialTheme.typography.titleMedium)
                                        user.number?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                                        user.mail?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                                        user.credit?.let { Text("Credit: $it", style = MaterialTheme.typography.bodySmall) }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
