package com.bikeshare.app.util

import com.bikeshare.app.data.api.dto.ApiEnvelope
import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

/**
 * Regression for spec 0011: the app showed a raw "HTTP 409" on rent rejection instead of
 * the problem+json reason. The error mapper must surface `detail` + `code` whenever the
 * body carries them, even if the body omits some RFC-7807 fields, and fall back to the
 * generic HTTP line only when the body is genuinely absent/unparseable.
 */
class SafeApiCallErrorTest {

    private val moshi = Moshi.Builder().build()

    private fun mapError(code: Int, body: String): NetworkResult.Error {
        val responseBody = body.toResponseBody("application/problem+json".toMediaType())
        val result = runBlocking {
            safeApiCall<Any>(moshi) { Response.error(code, responseBody) }
        }
        return result as NetworkResult.Error
    }

    @Test
    fun `full problem+json surfaces detail and code`() {
        val body = """
            {"type":"about:blank","title":"Conflict","status":409,
             "detail":"This bike is on a service stand and can't be rented.",
             "instance":"/api/v1/bikes/rent","requestId":"abc",
             "code":"bike.rent.error.service_stand","params":{}}
        """.trimIndent()

        val error = mapError(409, body)

        assertEquals("This bike is on a service stand and can't be rented.", error.message)
        assertEquals("bike.rent.error.service_stand", error.messageCode)
        assertEquals(409, error.code)
    }

    @Test
    fun `partial problem+json still surfaces detail and code`() {
        // A body that omits some RFC-7807 fields (type/title/instance/requestId) must not
        // make the mapper discard the reason and fall back to "HTTP 409".
        val body = """
            {"status":409,
             "detail":"This bike is on a service stand and can't be rented.",
             "code":"bike.rent.error.service_stand"}
        """.trimIndent()

        val error = mapError(409, body)

        assertEquals("This bike is on a service stand and can't be rented.", error.message)
        assertEquals("bike.rent.error.service_stand", error.messageCode)
    }

    @Test
    fun `empty body falls back to the generic http line`() {
        val error = mapError(409, "")

        assertTrue(error.message.startsWith("HTTP 409"))
        assertNull(error.messageCode)
    }

    @Test
    fun `non-json body falls back to the generic http line`() {
        val error = mapError(500, "Internal Server Error")

        assertTrue(error.message.startsWith("HTTP 500"))
        assertNull(error.messageCode)
    }
}
