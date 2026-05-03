package com.bikeshare.app.ui.navigation

import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ApplyQrResultTest {

    @Test
    fun `rent URL writes qr_action and qr_bike_number to handle`() {
        val handle = SavedStateHandle()
        applyQrResult("https://whitebikes.info/scan.php/rent/42", handle)
        assertEquals("rent", handle.get<String>("qr_action"))
        assertEquals(42, handle.get<Int>("qr_bike_number"))
        assertNull(handle.get<String>("qr_stand_name"))
        assertNull(handle.get<String>("qr_unknown_raw"))
    }

    @Test
    fun `return URL writes qr_action and qr_stand_name to handle`() {
        val handle = SavedStateHandle()
        applyQrResult("https://whitebikes.info/scan.php/return/MAIN", handle)
        assertEquals("return", handle.get<String>("qr_action"))
        assertEquals("MAIN", handle.get<String>("qr_stand_name"))
        assertNull(handle.get<Int>("qr_bike_number"))
        assertNull(handle.get<String>("qr_unknown_raw"))
    }

    @Test
    fun `unknown QR writes raw value to qr_unknown_raw`() {
        val handle = SavedStateHandle()
        applyQrResult("https://example.com/something/else", handle)
        assertEquals("https://example.com/something/else", handle.get<String>("qr_unknown_raw"))
        assertNull(handle.get<String>("qr_action"))
    }

    @Test
    fun `unknown QR clears any prior rent action left in the handle`() {
        val handle = SavedStateHandle()
        applyQrResult("https://whitebikes.info/scan.php/rent/42", handle)
        applyQrResult("https://example.com/garbage", handle)
        assertNull(handle.get<String>("qr_action"))
        assertNull(handle.get<Int>("qr_bike_number"))
        assertEquals("https://example.com/garbage", handle.get<String>("qr_unknown_raw"))
    }

    @Test
    fun `rent QR clears any prior return action left in the handle`() {
        val handle = SavedStateHandle()
        applyQrResult("https://whitebikes.info/scan.php/return/MAIN", handle)
        applyQrResult("https://whitebikes.info/scan.php/rent/7", handle)
        assertEquals("rent", handle.get<String>("qr_action"))
        assertEquals(7, handle.get<Int>("qr_bike_number"))
        assertNull(handle.get<String>("qr_stand_name"))
    }
}
