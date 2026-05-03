package com.bikeshare.app.domain.update

/**
 * Outcome of an update check. Distinguishing [Failed] from [UpToDate] matters for
 * UX — telling a user "you have the latest version" when the network call actually
 * errored masks bugs and breeds distrust (the user keeps seeing "up to date" while
 * everyone else got the new release).
 */
sealed interface UpdateCheckResult {
    data class Available(val info: UpdateInfo) : UpdateCheckResult
    data object UpToDate : UpdateCheckResult
    data object Failed : UpdateCheckResult
    data object Disabled : UpdateCheckResult
}
