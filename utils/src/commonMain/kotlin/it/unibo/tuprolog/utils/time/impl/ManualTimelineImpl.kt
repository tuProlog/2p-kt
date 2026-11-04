package it.unibo.tuprolog.utils.time.impl

import it.unibo.tuprolog.utils.synchronizedOn
import it.unibo.tuprolog.utils.time.ManualTimeline
import kotlin.time.Duration
import kotlin.time.TestTimeSource
import kotlin.time.TimeSource

/** A [ManualTimeline] over a [TestTimeSource]; advancing is synchronized, so concurrent advances all add up. */
internal class ManualTimelineImpl(
    private val testSource: TestTimeSource,
) : ManualTimeline {
    private val origin = testSource.markNow()

    override val source: TimeSource.WithComparableMarks
        get() = testSource

    override val now: Duration
        get() = testSource.markNow() - origin

    override fun advanceBy(duration: Duration) {
        require(!duration.isNegative()) { "Time can't go backwards: $duration" }
        synchronizedOn(this) { testSource += duration }
    }

    override fun waitFor(duration: Duration) {
        if (duration.isPositive()) advanceBy(duration)
    }

    override fun toString(): String = "ManualTimeline(now=$now)"
}
