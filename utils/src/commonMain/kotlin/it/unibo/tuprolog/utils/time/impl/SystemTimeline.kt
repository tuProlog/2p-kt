package it.unibo.tuprolog.utils.time.impl

import it.unibo.tuprolog.utils.time.Timeline
import kotlin.time.Duration
import kotlin.time.TimeSource

/** The platform's monotonic clock, waited on by blocking (see [blockFor]). */
internal object SystemTimeline : Timeline {
    override val source: TimeSource.WithComparableMarks
        get() = TimeSource.Monotonic

    override fun waitFor(duration: Duration) = blockFor(duration)

    override fun toString(): String = "SystemTimeline"
}
