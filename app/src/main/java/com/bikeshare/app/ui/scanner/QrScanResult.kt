package com.bikeshare.app.ui.scanner

/**
 * Parsed result from scanning a bike or stand QR code.
 * Backend generates: bike URL .../scan.php/rent/{bikeNumber}, stand URL .../scan.php/return/{standName}.
 */
sealed class QrScanResult {
    data class Rent(val bikeNumber: Int) : QrScanResult()
    data class Return(val standName: String) : QrScanResult()
    data object Unknown : QrScanResult()
}

fun parseQrScanResult(raw: String): QrScanResult {
    val trimmed = raw.trim()
    if (trimmed.isEmpty()) return QrScanResult.Unknown

    // URL format: .../rent/123 or .../scan.php/rent/123
    val rentMatch = Regex("""/rent/(\d+)/?$""").find(trimmed)
    if (rentMatch != null) {
        val num = rentMatch.groupValues.getOrNull(1)?.toIntOrNull()
        if (num != null) return QrScanResult.Rent(num)
    }

    // URL format: .../return/STANDNAME (stand names are typically uppercase letters)
    val returnMatch = Regex("""/return/([^/]+)/?$""").find(trimmed)
    if (returnMatch != null) {
        val stand = returnMatch.groupValues.getOrNull(1)?.trim()
        if (!stand.isNullOrBlank()) return QrScanResult.Return(stand)
    }

    // Fallback: plain number -> bike number
    if (trimmed.all { it.isDigit() } && trimmed.isNotEmpty()) {
        trimmed.toIntOrNull()?.let { return QrScanResult.Rent(it) }
    }

    return QrScanResult.Unknown
}
