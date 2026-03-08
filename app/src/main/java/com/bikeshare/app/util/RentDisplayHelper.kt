package com.bikeshare.app.util

import com.bikeshare.app.data.api.dto.RentSystemResultDto

/**
 * Sanitizes server message that may contain HTML (e.g. &lt;br /&gt;) for plain-text display.
 */
fun sanitizeMessage(message: String?): String? {
    if (message.isNullOrBlank()) return null
    return message
        .replace("<br />", "\n")
        .replace("<br>", "\n")
        .replace("<br/>", "\n")
        .trim()
}

/** Strips all HTML tags for plain-text display. */
fun stripAllHtml(text: String?): String? {
    if (text.isNullOrBlank()) return null
    return text.replace(Regex("<[^>]+>"), " ").replace(Regex("\\s+"), " ").trim()
}

/**
 * Builds rent success text from params only (no raw HTML from server message).
 */
fun buildRentDisplayText(dto: RentSystemResultDto): String {
    val p = dto.params
    val parts = mutableListOf<String>()
    p?.bikeNumber?.let { parts.add("Bike #$it") }
    p?.currentCode?.let { parts.add("Lock code: $it") }
    p?.newCode?.let { parts.add("New code: $it") }
    p?.note?.takeIf { it.isNotBlank() }?.let { parts.add("Note: $it") }
    val fromParams = if (parts.isNotEmpty()) parts.joinToString("\n") else ""
    if (fromParams.isNotEmpty()) return fromParams
    return stripAllHtml(dto.message).orEmpty().ifBlank { "Rent successful." }
}

/**
 * Builds user-facing text for return success from code + params (avoids raw HTML in message).
 * Includes the lock code to close the bike when available.
 */
fun buildReturnDisplayMessage(dto: RentSystemResultDto, fallbackStandName: String? = null): String {
    val p = dto.params
    if (p != null) {
        val bikeNum = p.bikeNumber ?: 0
        val standName = p.standName ?: fallbackStandName ?: ""
        val main = "Bike #$bikeNum returned to $standName"
        val closeCode = p.newCode?.takeIf { it.isNotBlank() }?.let { " Close the lock with code: $it." }
            ?: p.currentCode?.takeIf { it.isNotBlank() }?.let { " Close the lock with code: $it." }
            ?: ""
        val credit = when {
            p.creditChange != null && p.creditChange != 0.0 -> {
                val currency = p.creditCurrency?.takeIf { it.isNotBlank() } ?: ""
                " Credit change: ${p.creditChange} $currency".trim()
            }
            else -> ""
        }
        val note = p.note?.takeIf { it.isNotBlank() }?.let { " Note: $it" } ?: ""
        return main + closeCode + credit + note
    }
    return sanitizeMessage(dto.message) ?: (fallbackStandName?.let { "Bike returned to $it" } ?: "Bike returned.")
}
