package com.bikeshare.app.domain.update

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateCheckerTest {

    @Test
    fun `1_0_6 sees 1_1_0 as newer`() {
        // Reproduces the reported bug: a user on 1.0.6 must be notified of 1.1.0.
        assertTrue(UpdateChecker.isNewer(latest = "1.1.0", current = "1.0.6"))
    }

    @Test
    fun `same version is not newer`() {
        assertFalse(UpdateChecker.isNewer(latest = "1.1.0", current = "1.1.0"))
    }

    @Test
    fun `older version is not newer`() {
        assertFalse(UpdateChecker.isNewer(latest = "1.0.5", current = "1.0.6"))
    }

    @Test
    fun `patch bump is detected`() {
        assertTrue(UpdateChecker.isNewer(latest = "1.0.7", current = "1.0.6"))
    }

    @Test
    fun `major bump is detected`() {
        assertTrue(UpdateChecker.isNewer(latest = "2.0.0", current = "1.99.99"))
    }

    @Test
    fun `shorter latest matches with implicit zeros`() {
        assertFalse(UpdateChecker.isNewer(latest = "1.0", current = "1.0.1"))
        assertTrue(UpdateChecker.isNewer(latest = "1.1", current = "1.0.99"))
    }

    @Test
    fun `non-numeric components are skipped`() {
        // "1.0.6-rc1" splits to ["1","0","6-rc1"] — last toIntOrNull() returns null and is dropped.
        // So "1.0.6-rc1" parses as [1,0] which compares less than [1,0,6].
        assertFalse(UpdateChecker.isNewer(latest = "1.0.6-rc1", current = "1.0.6"))
    }
}
