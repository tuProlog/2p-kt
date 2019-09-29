package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.SideEffectManager

/**
 * Interface adding some more fields to [ExecutionContext] specific for [SolverSLD] implementation
 *
 * @author Enrico
 */
internal interface DeclarativeImplExecutionContext<S : SideEffectManager> : ExecutionContext {

    /** The key strategies that a solver should use during resolution process */
    val solverStrategies: SolverStrategies

    /** The side effects manager to be used during resolution process */
    val sideEffectManager: S

}
