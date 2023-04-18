package it.unibo.tuprolog.solve

import it.unibo.tuprolog.Info
import it.unibo.tuprolog.Os

interface SolverTest {
    /** A short test max duration */
    val shortDuration: TimeDuration
        get() = 1000L * OS_SPECIFIC_TIME_MULTIPLIER

    /** A medium test max duration */
    val mediumDuration: TimeDuration
        get() = 2 * shortDuration

    /** A long test max duration */
    val longDuration: TimeDuration
        get() = 4 * mediumDuration

    companion object {
        private val OS_SPECIFIC_TIME_MULTIPLIER: Int = when (Info.OS) {
            Os.WINDOWS -> 2
            else -> 1
        }
    }
}
