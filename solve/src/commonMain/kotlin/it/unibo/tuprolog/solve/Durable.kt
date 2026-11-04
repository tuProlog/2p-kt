package it.unibo.tuprolog.solve

import it.unibo.tuprolog.utils.time.Chronometer
import kotlin.js.JsName
import kotlin.time.Duration

/**
 * A type for entities bound to a maximum execution duration, e.g. an [ExecutionContext] or a
 * [it.unibo.tuprolog.solve.primitive.Solve.Request]: their timing is that of the [chronometer] of the resolution
 * they belong to, which enforces [SolveOptions.timeout] across the resolution and its nested requests.
 */
interface Durable {
    /**
     * The chronometer of the resolution this entity belongs to. It is resolution-level, non-backtrackable state:
     * every context of one resolution shares it, so backtracking neither restores elapsed time nor replenishes
     * the budget.
     */
    @JsName("chronometer")
    val chronometer: Chronometer

    /** The maximum duration the resolution is allowed to run for ([Duration.INFINITE] if unlimited). */
    @JsName("maxDuration")
    val maxDuration: Duration
        get() = chronometer.timeout

    /** How much time elapsed since the resolution started, read from the clock. */
    @JsName("elapsedTime")
    val elapsedTime: Duration
        get() = chronometer.elapsed()

    /** How much time is left before the resolution times out (never negative), read from the clock. */
    @JsName("remainingTime")
    val remainingTime: Duration
        get() = chronometer.remaining()
}
