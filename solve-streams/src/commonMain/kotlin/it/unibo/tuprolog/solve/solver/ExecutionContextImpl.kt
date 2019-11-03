package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.solve.PrologFlags
import it.unibo.tuprolog.theory.ClauseDatabase

/**
 * The execution context implementation for [SolverSLD]
 *
 * @author Enrico
 */
internal data class ExecutionContextImpl(
        override val libraries: Libraries = Libraries(),
        override val flags: PrologFlags = emptyMap(),
        override val staticKB: ClauseDatabase = ClauseDatabase.empty(),
        override val dynamicKB: ClauseDatabase = ClauseDatabase.empty(),
        override val substitution: Substitution.Unifier = Substitution.empty(),
        override val solverStrategies: SolverStrategies = SolverStrategies.prologStandard,
        override val sideEffectManager: SideEffectManagerImpl = SideEffectManagerImpl()
) : DeclarativeImplExecutionContext<SideEffectManagerImpl> {

    override val prologStackTrace: Sequence<Struct> by lazy { sideEffectManager.logicalParentRequests.map { it.query } }

}
