package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.PrologFlags
import it.unibo.tuprolog.solve.StreamsSolver
import it.unibo.tuprolog.solve.SolverStrategies
import it.unibo.tuprolog.theory.ClauseDatabase

/**
 * The execution context implementation for [StreamsSolver]
 *
 * @author Enrico
 */
internal data class ExecutionContextImpl(
    override val libraries: Libraries = Libraries(),
    override val flags: PrologFlags = emptyMap(),
    override val staticKB: ClauseDatabase = ClauseDatabase.empty(),
    override val dynamicKB: ClauseDatabase = ClauseDatabase.empty(),
    override val substitution: Substitution.Unifier = Substitution.empty(),
    /** The key strategies that a solver should use during resolution process */
    val solverStrategies: SolverStrategies = SolverStrategies.prologStandard,
    /** The side effects manager to be used during resolution process */
    val sideEffectManager: SideEffectManagerImpl = SideEffectManagerImpl()
) : ExecutionContext {

    override val prologStackTrace: Sequence<Struct> by lazy { sideEffectManager.logicalParentRequests.value.map { it.query } }
}

/** Extension method to get [SideEffectManagerImpl], if this context is of right type*/
internal fun ExecutionContext.getSideEffectManager(): SideEffectManagerImpl? =
    (this as? ExecutionContextImpl)?.sideEffectManager
