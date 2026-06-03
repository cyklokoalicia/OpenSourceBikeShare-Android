package com.bikeshare.app.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Spec 0006: tapping a bike in a stand's list opens its detail — for admins only.
 * The handler is the single source of that rule; the UI shows a tappable (rippling)
 * row exactly when a handler is present.
 */
class AdminBikeClickTest {

    @Test
    fun `admin gets a handler that navigates to that bike's detail`() {
        val navigated = mutableListOf<String>()
        val handler = adminBikeClick(isAdmin = true) { navigated += it }

        assertNotNull(handler)
        handler!!(42)
        assertEquals(listOf("admin/bikes/42"), navigated)
    }

    @Test
    fun `non-admin gets no handler so the row is not actionable`() {
        val navigated = mutableListOf<String>()
        val handler = adminBikeClick(isAdmin = false) { navigated += it }

        assertNull(handler)
        assertTrue(navigated.isEmpty())
    }
}
