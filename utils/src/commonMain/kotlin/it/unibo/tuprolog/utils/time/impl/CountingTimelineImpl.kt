package it.unibo.tuprolog.utils.time.impl

import it.unibo.tuprolog.utils.synchronizedOn
import it.unibo.tuprolog.utils.time.CountingTimeline
import it.unibo.tuprolog.utils.time.Timeline
import kotlin.time.ComparableTimeMark
import kotlin.time.Duration
import kotlin.time.TimeSource

/** A [CountingTimeline] decorating [delegate]: waiting is delegated as is, and is not counted. */
internal class CountingTimelineImpl(
    private val delegate: Timeline,
) : CountingTimeline {
    private var count: Long = 0

    override val reads: Long
        get() = synchronizedOn(this) { count }

    override val source: TimeSource.WithComparableMarks =
        object : TimeSource.WithComparableMarks {
            override fun markNow(): ComparableTimeMark {
                synchronizedOn(this@CountingTimelineImpl) { count++ }
                return delegate.source.markNow()
            }
        }

    override fun waitFor(duration: Duration) = delegate.waitFor(duration)

    override fun toString(): String = "CountingTimeline(reads=$reads, delegate=$delegate)"
}
