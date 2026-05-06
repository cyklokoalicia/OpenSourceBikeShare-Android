package com.bikeshare.app.ui.admin

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.bikeshare.app.R

/**
 * Status palette mirroring the web admin UI (Bootstrap utility colors).
 * Kept light enough for white-on-color text to remain legible on the card
 * header strip (or background tint on bike cards).
 */
data class StatusPalette(val container: Color, val onContainer: Color)

private val Success = StatusPalette(container = Color(0xFF4CAF50), onContainer = Color.White)
private val Warning = StatusPalette(container = Color(0xFFFF9800), onContainer = Color.Black)
private val Info = StatusPalette(container = Color(0xFF17A2B8), onContainer = Color.White)
private val Secondary = StatusPalette(container = Color(0xFF6C757D), onContainer = Color.White)
private val Primary = StatusPalette(container = Color(0xFF2196F3), onContainer = Color.White)

/**
 * Single source of truth for an admin status — used by filter chip rows AND
 * card visuals. Adding a new status means appending one entry here.
 *
 * `palette = null` means "no special tint, fall back to MaterialTheme surface"
 * — used for OK bikes so their cards look neutral (mirroring web's `bg-light`).
 */
data class StatusOption(
    val key: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector?,
    val palette: StatusPalette?,
)

val StandStatusOptions: List<StatusOption> = listOf(
    StatusOption("active", R.string.stand_status_active, null, Success),
    StatusOption("technical", R.string.stand_status_technical, Icons.Default.Build, Warning),
    StatusOption("hidden", R.string.stand_status_hidden, Icons.Default.VisibilityOff, Info),
    StatusOption("inactive", R.string.stand_status_inactive, Icons.Default.Block, Secondary),
    StatusOption("virtual", R.string.stand_status_virtual, Icons.Default.Public, Primary),
)

val BikeStatusOptions: List<StatusOption> = listOf(
    StatusOption("problematic", R.string.bike_status_problematic, Icons.Default.Warning, Warning),
    StatusOption("rented", R.string.bike_status_rented, Icons.Default.Person, Success),
    StatusOption("ok", R.string.bike_status_ok, Icons.AutoMirrored.Filled.DirectionsBike, null),
)

fun standStatusOption(status: String?): StatusOption? =
    status?.let { key -> StandStatusOptions.firstOrNull { it.key == key } }

fun bikeStatusOption(status: String?): StatusOption? =
    status?.let { key -> BikeStatusOptions.firstOrNull { it.key == key } }
