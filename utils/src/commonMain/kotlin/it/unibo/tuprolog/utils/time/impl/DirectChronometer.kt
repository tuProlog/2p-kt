package it.unibo.tuprolog.utils.time.impl

import it.unibo.tuprolog.utils.time.Chronometer
import it.unibo.tuprolog.utils.time.Timeline
import kotlin.time.ComparableTimeMark
import kotlin.time.Duration

/** A [Chronometer] reading the clock at every [leap] (unless the timeout is infinite): the reference one. */
internal class DirectChronometer(
    timeline: Timeline,
    timeout: Duration,
    start: ComparableTimeMark? = null,
    expired: Boolean = false,
    scope: ChildScope? = null,
) : AbstractChronometer(timeline, timeout, start, expired, scope) {
    override fun leap(): Boolean = checkNow()

    override fun newScope(
        timeout: Duration,
        start: ComparableTimeMark,
        expired: Boolean,
        scope: ChildScope,
    ): Chronometer = DirectChronometer(timeline, timeout, start, expired, scope)
}
