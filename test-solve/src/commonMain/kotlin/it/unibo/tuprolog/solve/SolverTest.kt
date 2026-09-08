package it.unibo.tuprolog.solve

import it.unibo.tuprolog.Info
import it.unibo.tuprolog.Os

/**
 * Base interface shared by every reusable, solver-agnostic conformance test suite in this module (e.g. [TestSolver],
 * [TestAbolish], [TestFindAll], and so on).
 *
 * It only provides the [TimeDuration]s ([shortDuration], [mediumDuration], [longDuration]) that test cases pass to
 * `Solver.solve(goal, maxDuration)` so that a single set of ISO-conformance tests can be shared, via the `prototype`
 * factory declared on each subinterface's companion object, by every concrete `Solver` implementation
 * (`:solve-classic`, `:solve-streams`, `:solve-concurrent`) instead of being duplicated once per module. Durations are
 * scaled up on Windows (see [Info.OS]), where CI runners have historically needed more slack to reliably finish
 * within the allotted time.
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
