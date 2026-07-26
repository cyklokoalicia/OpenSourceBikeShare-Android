package com.bikeshare.app.data.api

import com.bikeshare.app.util.SessionEvent
import com.bikeshare.app.util.SessionEventBus
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Test

class UpgradeRequiredInterceptorTest {

    /**
     * Fires one request answered with [code]; returns every event the bus emitted.
     */
    private fun eventsFor(code: Int): List<SessionEvent> = runBlocking {
        val bus = SessionEventBus()
        val received = mutableListOf<SessionEvent>()
        val collector = launch(start = CoroutineStart.UNDISPATCHED) {
            bus.events.collect { received += it }
        }

        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(code))
            server.start()
            val client = OkHttpClient.Builder().addInterceptor(UpgradeRequiredInterceptor(bus)).build()
            client.newCall(Request.Builder().url(server.url("/")).build()).execute().close()
        }

        yield()
        collector.cancelAndJoin()
        received
    }

    @Test
    fun `emits UpdateRequired on 426`() =
        assertEquals(listOf(SessionEvent.UpdateRequired), eventsFor(426))

    @Test
    fun `does not emit on other codes`() =
        assertEquals(emptyList<SessionEvent>(), eventsFor(200))
}
