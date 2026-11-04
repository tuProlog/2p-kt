package it.unibo.tuprolog.utils.time

import kotlin.time.Duration

/**
 * A simulated [Timeline], for deterministic tests: time stands still unless explicitly [advanced][advanceBy], and
 * [waitFor] advances it rather than waiting. Reading the time never makes it advance.
 */
interface ManualTimeline : Timeline {
    /** How much time has elapsed on this timeline, since its creation. */
    val now: Duration

    /** Moves time forward by [duration], which must not be negative. */
    fun advanceBy(duration: Duration)
}
