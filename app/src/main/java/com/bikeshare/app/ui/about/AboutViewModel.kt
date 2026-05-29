package com.bikeshare.app.ui.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikeshare.app.domain.model.MessengerChat
import com.bikeshare.app.domain.repository.AuthRepository
import com.bikeshare.app.domain.update.UpdateCheckResult
import com.bikeshare.app.domain.update.UpdateChecker
import com.bikeshare.app.domain.update.UpdateInfo
import com.bikeshare.app.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class AboutUiState(
    val updateInfo: UpdateInfo? = null,
    val isChecking: Boolean = false,
    val checkFailed: Boolean = false,
    val messengerChats: List<MessengerChat> = emptyList(),
)

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val updateChecker: UpdateChecker,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AboutUiState())
    val uiState: StateFlow<AboutUiState> = _uiState.asStateFlow()

    init {
        checkForUpdate()
        loadMessengerChats()
    }

    fun checkForUpdate() {
        viewModelScope.launch {
            _uiState.update { it.copy(isChecking = true, checkFailed = false) }
            // force = true: bypass the 24h throttle so user sees fresh result on demand
            val result = updateChecker.checkForUpdate(force = true)
            _uiState.update {
                it.copy(
                    updateInfo = (result as? UpdateCheckResult.Available)?.info,
                    isChecking = false,
                    checkFailed = result is UpdateCheckResult.Failed,
                )
            }
        }
    }

    private fun loadMessengerChats() {
        viewModelScope.launch {
            when (val result = authRepository.getMessengerChats()) {
                is NetworkResult.Success ->
                    _uiState.update { it.copy(messengerChats = result.data) }
                is NetworkResult.Error ->
                    Timber.w("Failed to load messenger chats: ${result.message}")
                is NetworkResult.Loading -> Unit
            }
        }
    }
}
