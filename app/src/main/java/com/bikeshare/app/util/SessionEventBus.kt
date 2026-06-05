package com.bikeshare.app.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/** App-wide, session-scoped events surfaced from the network layer to the UI. */
sealed interface SessionEvent {
    /** The server rejected a request because the user's phone is not confirmed. */
    data object PhoneUnconfirmed : SessionEvent

    /**
     * The server rejected a request with `426 Upgrade Required` because the client is
     * below the configured minimum supported version (spec 0005). The UI must block.
     */
    data object UpdateRequired : SessionEvent

    /**
     * The session is genuinely gone — the refresh token was rejected (or absent), so the
     * stored tokens were cleared (spec 0015). The UI must route to the login screen
     * instead of looping on token-less "Authentication required" responses.
     */
    data object SessionExpired : SessionEvent
}

/**
 * Single hop from the network layer (interceptors) to the UI (navigation). The UI
 * observes [events] and reacts (e.g. routes to the phone-verify screen) without any
 * ViewModel having to special-case the error.
 */
@Singleton
class SessionEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<SessionEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<SessionEvent> = _events.asSharedFlow()

    fun emit(event: SessionEvent) {
        _events.tryEmit(event)
    }
}
