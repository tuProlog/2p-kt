package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.solve.Solve
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

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
        fun sld(executionStrategy: CoroutineScope = CoroutineScope(Dispatchers.Default)): Solver =
                SolverSLD(executionStrategy)
    }
}
