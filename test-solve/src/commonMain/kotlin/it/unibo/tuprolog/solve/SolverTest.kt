package it.unibo.tuprolog.solve

interface SolverTest {
    /** A short test max duration */
    val shortDuration: TimeDuration
        get() = 250L

    /** A medium test max duration */
    val mediumDuration: TimeDuration
        get() = 2 * shortDuration

    /** A long test max duration */
    val longDuration: TimeDuration
        get() = 4 * mediumDuration
}