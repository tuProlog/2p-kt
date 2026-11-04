package it.unibo.tuprolog.utils.time

import it.unibo.tuprolog.utils.time.impl.CountingTimelineImpl
import it.unibo.tuprolog.utils.time.impl.ManualTimelineImpl
import it.unibo.tuprolog.utils.time.impl.SystemTimeline
import kotlin.jvm.JvmStatic
import kotlin.time.Duration
import kotlin.time.TestTimeSource
import kotlin.time.TimeSource

/**
 * A notion of time: a [source] of time marks, and a way of [waiting][waitFor] on that very same time line.
 *
 * Measuring and waiting are distinct concerns, but they must agree: waiting for some duration on a [Timeline]
 * must make (at least) that duration elapse, as measured by its [source]. A [Chronometer] relies on this to
 * implement [Chronometer.sleep] consistently with its own timeout.
 */
interface Timeline {
    /** The source of time marks, from which elapsed durations are measured. */
    val source: TimeSource.WithComparableMarks

    /**
     * Waits until (at least) [duration] has elapsed according to [source]. Non-positive durations return
     * immediately. Whether this blocks the caller, or just moves time forward, is up to the implementation.
     */
    fun waitFor(duration: Duration)

    companion object {
        /**
         * The platform's monotonic clock ([TimeSource.Monotonic]). Waiting blocks the calling thread on the JVM,
         * and busy-waits on JS, whose synchronous execution model offers no other way to block.
         */
        @JvmStatic
        val system: Timeline
            get() = SystemTimeline

        /** A simulated [ManualTimeline] over [source], where time only moves when explicitly advanced. */
        @JvmStatic
        fun manual(source: TestTimeSource = TestTimeSource()): ManualTimeline = ManualTimelineImpl(source)

        /** A [CountingTimeline] decorating [delegate], counting how many times its clock is read. */
        @JvmStatic
        fun counting(delegate: Timeline): CountingTimeline = CountingTimelineImpl(delegate)
    }
}
