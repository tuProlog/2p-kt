package it.unibo.tuprolog.utils.time.impl

import it.unibo.tuprolog.utils.time.Chronometer
import it.unibo.tuprolog.utils.time.ManualTimeline
import kotlin.time.ComparableTimeMark
import kotlin.time.Duration

/**
 * A [Chronometer] on a [ManualTimeline], whose every [leap] advances time by [durationPerLeap] (possibly zero)
 * before checking the timeout, as a [DirectChronometer] would.
 */
internal class SimulatedChronometer(
    private val manualTimeline: ManualTimeline,
    timeout: Duration,
    private val durationPerLeap: Duration,
    start: ComparableTimeMark? = null,
    expired: Boolean = false,
) : AbstractChronometer(manualTimeline, timeout, start, expired) {
    init {
        require(!durationPerLeap.isNegative()) { "The duration per leap can't be negative: $durationPerLeap" }
    }

    override fun leap(): Boolean {
        if (isExpired) return true
        if (durationPerLeap.isPositive()) manualTimeline.advanceBy(durationPerLeap)
        return checkNow()
    }

    override fun newScope(
        timeout: Duration,
        start: ComparableTimeMark,
        expired: Boolean,
    ): Chronometer = SimulatedChronometer(manualTimeline, timeout, durationPerLeap, start, expired)
}
