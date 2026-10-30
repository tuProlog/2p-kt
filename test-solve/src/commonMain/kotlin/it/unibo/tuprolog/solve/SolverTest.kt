package it.unibo.tuprolog.solve

import it.unibo.tuprolog.Info
import it.unibo.tuprolog.Os

/**
 * The [TimeDuration]s ([shortDuration], [mediumDuration], [longDuration]) that conformance cases pass to
 * `Solver.solve(goal, maxDuration)`. Shared solver contracts get them through [ContractScope]; the per-module suites
 * that still implement this interface (e.g. `:solve-concurrent`'s) get them directly. Durations are scaled up on
 * Windows (see [Info.OS]), where CI runners have historically needed more slack to reliably finish within the
 * allotted time.
 */
interface SolverTest {
    /** A short test max duration, used for queries expected to resolve almost immediately. */
    val shortDuration: TimeDuration
        get() = 1000L * OS_SPECIFIC_TIME_MULTIPLIER

    /** A medium test max duration, twice [shortDuration]; used for queries involving a handful of resolution steps. */
    val mediumDuration: TimeDuration
        get() = 2 * shortDuration

    /** A long test max duration, four times [mediumDuration]; used for queries involving deeper search or recursion. */
    val longDuration: TimeDuration
        get() = 4 * mediumDuration

    companion object {
        private val OS_SPECIFIC_TIME_MULTIPLIER: Int =
            when (Info.OS) {
                Os.WINDOWS -> 2
                else -> 1
            }
    }
}
