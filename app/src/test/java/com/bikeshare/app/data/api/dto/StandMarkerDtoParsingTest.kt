package com.bikeshare.app.data.api.dto

import com.squareup.moshi.Moshi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Spec 0007: the stand photo is shown in the stand detail. The API already returns
 * `standPhoto` as a full URL on the markers/stand response, so the app reuses it — these
 * guard that the field deserializes (URL when set, null when absent).
 */
class StandMarkerDtoParsingTest {

    private val adapter = Moshi.Builder().build().adapter(StandMarkerDto::class.java)

    @Test
    fun `standPhoto is parsed when present`() {
        val json = """
            {"standId":7,"standName":"STAND7","latitude":48.1,"longitude":17.1,
             "bikeCount":3,"status":"active",
             "standPhoto":"https://example.com/stands/stand7.jpg"}
        """.trimIndent()

        val dto = adapter.fromJson(json)

        assertEquals("https://example.com/stands/stand7.jpg", dto?.standPhoto)
    }

    @Test
    fun `standPhoto is null when absent`() {
        val json = """{"standName":"STAND7","latitude":48.1,"longitude":17.1}"""

        val dto = adapter.fromJson(json)

        assertNull(dto?.standPhoto)
    }
}
