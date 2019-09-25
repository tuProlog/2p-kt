package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct

/**
 * Represents a Prolog Goal solver
 *
 * @author Enrico
 */
interface Solver {

    /** Solves the provided goal, returning lazily initialized sequence of solutions */
    fun solve(goal: Struct): Sequence<Solution> // TODO: 25/09/2019 add parameter to specify maxDuration of computation

}
