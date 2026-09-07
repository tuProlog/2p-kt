package it.unibo.tuprolog.solve

import kotlin.js.JsName

/**
 * A type for entities bound to a maximum execution duration, e.g. an [ExecutionContext] or a
 * [it.unibo.tuprolog.solve.primitive.Solve.Request], used to enforce [it.unibo.tuprolog.solve.SolveOptions.timeout]
 * across a resolution and its nested requests.
 */
interface Durable {
    /** The time instant this entity's execution started at. */
    @JsName("startTime")
    val startTime: TimeInstant

    /**
     * The time instant by which this entity's execution should be over, computed as [startTime] `+` [maxDuration]
     * (saturating to [TimeInstant.MAX_VALUE] on overflow).
     */
    @JsName("endTime")
    val endTime: TimeInstant
        get() = (startTime + maxDuration).let { if (it < 0L) TimeInstant.MAX_VALUE else it }

    /** How much time is left before [endTime], computed against the current time instant. */
    @JsName("remainingTime")
    val remainingTime: TimeDuration
        get() = endTime - currentTimeInstant()

    /** How much time elapsed since [startTime], computed against the current time instant. */
    @JsName("elapsedTime")
    val elapsedTime: TimeDuration
        get() = currentTimeInstant() - startTime

    /** The maximum duration this entity's execution is allowed to run for, starting from [startTime]. */
    @JsName("maxDuration")
    val maxDuration: TimeDuration
}
