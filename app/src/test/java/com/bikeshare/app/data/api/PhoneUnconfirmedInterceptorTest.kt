package com.bikeshare.app.data.api

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Robolectric supplies a real org.json implementation (the JVM stub would throw).
@RunWith(RobolectricTestRunner::class)
class PhoneUnconfirmedInterceptorTest {

    @Test
    fun `detects phone_unconfirmed code in problem+json body`() {
        val body = """
            {"type":"about:blank","title":"Forbidden","status":403,
             "detail":"Phone number must be confirmed.",
             "instance":"/api/v1/me/bikes","requestId":"abc","code":"phone_unconfirmed"}
        """.trimIndent()
        assertTrue(PhoneUnconfirmedInterceptor.isPhoneUnconfirmed(body))
    }

    @Test
    fun `ignores a generic forbidden body without the code`() {
        assertFalse(PhoneUnconfirmedInterceptor.isPhoneUnconfirmed("""{"detail":"Access denied"}"""))
    }

    @Test
    fun `ignores a different code`() {
        assertFalse(PhoneUnconfirmedInterceptor.isPhoneUnconfirmed("""{"code":"something_else"}"""))
    }

    @Test
    fun `ignores non-json, empty and null bodies`() {
        assertFalse(PhoneUnconfirmedInterceptor.isPhoneUnconfirmed("not json at all"))
        assertFalse(PhoneUnconfirmedInterceptor.isPhoneUnconfirmed(""))
        assertFalse(PhoneUnconfirmedInterceptor.isPhoneUnconfirmed(null))
    }
}
