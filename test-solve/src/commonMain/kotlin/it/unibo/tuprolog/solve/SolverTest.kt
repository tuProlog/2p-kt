package it.unibo.tuprolog.solve

import it.unibo.tuprolog.Info
import it.unibo.tuprolog.Os
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * The [Duration]s ([shortDuration], [mediumDuration], [longDuration]) that conformance cases pass to
 * `Solver.solve(goal, maxDuration)`, as real-time safety budgets for queries expected to terminate. Cases about
 * timeouts themselves use simulated time instead (see [TimeoutContract]'s helpers). Shared solver contracts get them through [ContractScope]; the per-module suites
 * that still implement this interface (e.g. `:solve-concurrent`'s) get them directly. Durations are scaled up on
 * Windows (see [Info.OS]), where CI runners have historically needed more slack to reliably finish within the
 * allotted time.
 */
interface SolverTest {
    /** A short test max duration, used for queries expected to resolve almost immediately. */
    val shortDuration: Duration
        get() = 1.seconds * OS_SPECIFIC_TIME_MULTIPLIER

    /** A medium test max duration, twice [shortDuration]; used for queries involving a handful of resolution steps. */
    val mediumDuration: Duration
        get() = shortDuration * 2

    /** A long test max duration, four times [mediumDuration]; used for queries involving deeper search or recursion. */
    val longDuration: Duration
        get() = mediumDuration * 4

    companion object {
        private val OS_SPECIFIC_TIME_MULTIPLIER: Int =
            when (Info.OS) {
                Os.WINDOWS -> 2
                else -> 1
            }
    }
}
