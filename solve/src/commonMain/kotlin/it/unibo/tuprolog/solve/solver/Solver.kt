package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.statemachine.SolverSLD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

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
        fun sld(timeout: Long, executionScope: CoroutineScope = MainScope()): Solver =
                SolverSLD(timeout, executionScope)
    }
}
