package com.bikeshare.app.ui.scanner

import org.junit.Assert.assertEquals
import org.junit.Test

class QrScanResultTest {

    @Test
    fun `rent URL with absolute https scheme parses to Rent`() {
        val result = parseQrScanResult("https://whitebikes.info/scan.php/rent/42")
        assertEquals(QrScanResult.Rent(42), result)
    }

    @Test
    fun `rent URL with trailing slash parses to Rent`() {
        val result = parseQrScanResult("https://example.com/scan.php/rent/7/")
        assertEquals(QrScanResult.Rent(7), result)
    }

    @Test
    fun `return URL with stand name parses to Return`() {
        val result = parseQrScanResult("https://whitebikes.info/scan.php/return/MAIN")
        assertEquals(QrScanResult.Return("MAIN"), result)
    }

    @Test
    fun `return URL with mixed-case stand name preserves case`() {
        val result = parseQrScanResult("https://example.com/scan.php/return/Main-Square")
        assertEquals(QrScanResult.Return("Main-Square"), result)
    }

    @Test
    fun `bare digit string is treated as bike number`() {
        val result = parseQrScanResult("123")
        assertEquals(QrScanResult.Rent(123), result)
    }

    @Test
    fun `empty string yields Unknown`() {
        assertEquals(QrScanResult.Unknown, parseQrScanResult(""))
    }

    @Test
    fun `unrelated URL yields Unknown`() {
        assertEquals(QrScanResult.Unknown, parseQrScanResult("https://example.com/something/else"))
    }

    @Test
    fun `whitespace around input is trimmed`() {
        assertEquals(QrScanResult.Rent(5), parseQrScanResult("  https://x/rent/5  "))
    }
}
