package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.SideEffectManager

//TODO doc
interface DeclarativeImplExecutionContext : ExecutionContext {

    /** The key strategies that a solver should use during resolution process */
    val solverStrategies: SolverStrategies

    // TODO doc
    val sideEffectManager: SideEffectManager

}
