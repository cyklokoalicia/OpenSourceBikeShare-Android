package com.bikeshare.app.ui.navigation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikeshare.app.BuildConfig
import com.bikeshare.app.domain.repository.RentalRepository
import com.bikeshare.app.domain.update.UpdateCheckResult
import com.bikeshare.app.domain.update.UpdateChecker
import com.bikeshare.app.domain.update.UpdateInfo
import com.bikeshare.app.notification.FreeTimeNotificationScheduler
import com.bikeshare.app.util.NetworkResult
import com.bikeshare.app.util.SessionEvent
import com.bikeshare.app.util.SessionEventBus
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val rentalRepository: RentalRepository,
    private val updateChecker: UpdateChecker,
    sessionEventBus: SessionEventBus,
) : ViewModel() {

    /** Session events surfaced from the network layer (e.g. phone-unconfirmed gate). */
    val sessionEvents: SharedFlow<SessionEvent> = sessionEventBus.events

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    private val _updateInfo = MutableStateFlow<UpdateInfo?>(null)
    val updateInfo: StateFlow<UpdateInfo?> = _updateInfo.asStateFlow()

    /**
     * Non-null (a download URL) once the server has rejected a request with `426 Upgrade
     * Required` (spec 0005); the UI then blocks with the force-update screen. The gate is
     * server-enforced — we only react to it here. Once set it stays until the process
     * restarts on a new version.
     */
    private val _forceUpdateUrl = MutableStateFlow<String?>(null)
    val forceUpdateUrl: StateFlow<String?> = _forceUpdateUrl.asStateFlow()

    init {
        viewModelScope.launch {
            sessionEventBus.events.collect { event ->
                if (event is SessionEvent.UpdateRequired) onUpdateRequired()
            }
        }
    }

    private suspend fun onUpdateRequired() {
        if (_forceUpdateUrl.value != null) return
        // The 426 carries no URL — the download target is the latest GitHub release,
        // same source as the soft update. Fall back to the website if that fetch fails.
        _forceUpdateUrl.value = updateChecker.latestReleaseUrl() ?: BuildConfig.WEBSITE_URL
    }

    fun checkForUpdate() {
        viewModelScope.launch {
            val result = updateChecker.checkForUpdate()
            if (result is UpdateCheckResult.Available) {
                _updateInfo.value = result.info
            }
        }
    }

    fun dismissUpdate() {
        _updateInfo.value = null
    }

    fun loadLimits() {
        viewModelScope.launch {
            when (val result = rentalRepository.getMyLimits()) {
                is NetworkResult.Success -> {
                    _isAdmin.value = (result.data.privileges ?: 0) >= 1
                    scheduleFreeTimeNotifications(result.data.freeTimeMinutes ?: 30)
                }
                is NetworkResult.Error -> {
                    _isAdmin.value = false
                    FreeTimeNotificationScheduler.cancelAll(appContext)
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    private suspend fun scheduleFreeTimeNotifications(freeTimeMinutes: Int) {
        when (val bikesResult = rentalRepository.getMyBikes()) {
            is NetworkResult.Success -> {
                val bikes = bikesResult.data
                if (bikes.isEmpty()) {
                    FreeTimeNotificationScheduler.cancelAll(appContext)
                } else {
                    bikes.forEach { bike ->
                        val rentedSec = bike.rentedSeconds ?: 0
                        FreeTimeNotificationScheduler.schedule(
                            appContext,
                            bike.bikeNum,
                            rentedSec,
                            freeTimeMinutes,
                        )
                    }
                }
            }
            is NetworkResult.Error -> FreeTimeNotificationScheduler.cancelAll(appContext)
            is NetworkResult.Loading -> {}
        }
    }
}
