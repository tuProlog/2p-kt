package it.unibo.tuprolog.solve.timing

import it.unibo.tuprolog.solve.Solution

/** How far a timed resolution got, as seen by its consumer. */
enum class Outcome {
    /** The solutions ran out, the last one not being a [Solution.Halt]. */
    EXHAUSTED,

    /** The last solution was a [Solution.Halt]. */
    HALTED,

    /** The consumer stopped (or hasn't yet finished) before the solutions ran out. */
    ABANDONED,
}
