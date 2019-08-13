package it.unibo.tuprolog.solve.solver.statemachine

import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.AbstractSolver
import kotlinx.coroutines.CoroutineScope

/**
 * Default implementation of SLD (*Selective Linear Definite*) solver, exploring the search tree
 *
 * @author Enrico
 */
internal class SolverSLD(private val executionScope: CoroutineScope) : AbstractSolver() {

    override suspend fun solve(goal: Solve.Request): Sequence<Solve.Response> {
        generateSequence { }
        TODO("not implemented")
    }
}
