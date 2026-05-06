package com.bikeshare.app.ui.admin

import androidx.compose.ui.graphics.Color

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

private val StandStatusPalettes: Map<String, StatusPalette> = mapOf(
    "active" to Success,
    "technical" to Warning,
    "hidden" to Info,
    "inactive" to Secondary,
    "virtual" to Primary,
)

private val BikeStatusPalettes: Map<String, StatusPalette> = mapOf(
    "rented" to Success,
    "problematic" to Warning,
    "ok" to Secondary,
)

fun standStatusPalette(status: String?): StatusPalette? =
    status?.let { StandStatusPalettes[it] }

fun bikeStatusPalette(status: String): StatusPalette? = BikeStatusPalettes[status]
