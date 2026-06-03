package com.bikeshare.app.ui.map

import com.bikeshare.app.data.api.dto.StandMarkerDto
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Spec 0010: service ("technical") and testing ("hidden") stands must be drawn before
 * (below) active stands so an active stand is never hidden behind a service marker.
 */
class StandDrawOrderTest {

    private fun stand(name: String, status: String?) =
        StandMarkerDto(standName = name, latitude = 0.0, longitude = 0.0, status = status)

    @Test
    fun `service and testing stands are ordered before active ones`() {
        val stands = listOf(
            stand("active1", "active"),
            stand("service", "technical"),
            stand("active2", null),
            stand("testing", "hidden"),
        )

        val ordered = standsInDrawOrder(stands).map { it.standName }

        // layer 0 (service/testing) first, layer 1 (active) last
        assertEquals(listOf("service", "testing", "active1", "active2"), ordered)
    }

    @Test
    fun `fetch order is preserved within each layer (stable)`() {
        val stands = listOf(
            stand("a", "active"),
            stand("b", "active"),
            stand("s1", "technical"),
            stand("s2", "technical"),
        )

        val ordered = standsInDrawOrder(stands).map { it.standName }

        assertEquals(listOf("s1", "s2", "a", "b"), ordered)
    }

    @Test
    fun `layer mapping puts only technical and hidden below`() {
        assertEquals(0, standDrawLayer("technical"))
        assertEquals(0, standDrawLayer("hidden"))
        assertEquals(1, standDrawLayer("active"))
        assertEquals(1, standDrawLayer(null))
        assertEquals(1, standDrawLayer("inactive"))
    }
}
