package it.unibo.tuprolog.solve.mutable

import it.unibo.tuprolog.solve.Solve

/**
 * Represents a Prolog Goal solver
 *
 * @author Enrico
 */
interface Solver {

    /** Solves the provided goal, returning lazily initialized sequence of responses */
    fun solve(goal: Solve.Request): Sequence<Solve.Response>

    companion object {

        /** Creates an SLD (*Selective Linear Definite*) solver */
        fun sld(): Solver = SolverSLD()
    }
}
