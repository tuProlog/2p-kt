package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct

/**
 * Represents a Prolog Goal solver
 *
 * @author Enrico
 */
interface Solver {

    /** Solves the provided goal, returning lazily initialized sequence of solutions */
    fun solve(goal: Struct): Sequence<Solution>

}
