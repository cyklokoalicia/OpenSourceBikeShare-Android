package com.bikeshare.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bikeshare.app.data.local.UserPreferences
import com.bikeshare.app.notification.FreeTimeNotificationWorker
import com.bikeshare.app.ui.navigation.AppNavGraph
import com.bikeshare.app.ui.theme.BikeShareTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    private var pendingNavigation by mutableStateOf<String?>(null)
    private var pendingQrUrl by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        // Only consume on first launch — on configuration changes the same Intent is
        // redelivered, which would cause deep-link QR actions (rent/return) to fire twice.
        if (savedInstanceState == null) {
            consumeIntent(intent)
        }
        setContent {
            val themeFlow = remember(userPreferences) { userPreferences.themeModeFlow() }
            val themeMode by themeFlow.collectAsStateWithLifecycle(initialValue = userPreferences.themeMode)
            BikeShareTheme(themeMode = themeMode) {
                AppNavGraph(
                    navigateTo = pendingNavigation,
                    pendingQrUrl = pendingQrUrl,
                    onNavigationConsumed = { pendingNavigation = null },
                    onQrConsumed = { pendingQrUrl = null },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        consumeIntent(intent)
    }

    private fun consumeIntent(intent: Intent) {
        intent.getStringExtra(FreeTimeNotificationWorker.EXTRA_NAVIGATE_TO)?.let {
            pendingNavigation = it
        }
        if (intent.action == Intent.ACTION_VIEW) {
            intent.dataString?.let { pendingQrUrl = it }
        }
    }
}
