package com.bikeshare.app.ui.admin

import androidx.annotation.StringRes
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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

private val StandStatusByKey: Map<String, StatusOption> = StandStatusOptions.associateBy { it.key }
private val BikeStatusByKey: Map<String, StatusOption> = BikeStatusOptions.associateBy { it.key }

fun standStatusOption(status: String?): StatusOption? = status?.let(StandStatusByKey::get)

fun bikeStatusOption(status: String?): StatusOption? = status?.let(BikeStatusByKey::get)

/** Returns a copy of the set with `element` toggled (added if absent, removed if present). */
fun <T> Set<T>.toggleElement(element: T): Set<T> =
    toMutableSet().apply { if (!add(element)) remove(element) }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusFilterRow(
    options: List<StatusOption>,
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
        options.forEach { option ->
            FilterChip(
                selected = option.key in selected,
                onClick = { onToggle(option.key) },
                label = { Text(stringResource(option.labelRes)) },
                leadingIcon = option.icon?.let {
                    { Icon(it, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                },
            )
        }
    }
}
