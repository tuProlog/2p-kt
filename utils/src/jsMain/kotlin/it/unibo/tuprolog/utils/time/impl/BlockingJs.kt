package it.unibo.tuprolog.utils.time.impl

import kotlin.time.Duration
import kotlin.time.TimeSource

/**
 * Busy-waits: JS execution is single-threaded and synchronous here, so there is no way of blocking other than
 * spinning (a non-busy wait would need an asynchronous API).
 */
internal actual fun blockFor(duration: Duration) {
    if (!duration.isPositive()) return
    val wakeUp = TimeSource.Monotonic.markNow() + duration
    while (wakeUp.hasNotPassedNow()) {
        // spinning, on purpose
    }
}
