package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.SolverSLD
import it.unibo.tuprolog.solve.SolverStrategies
import it.unibo.tuprolog.theory.ClauseDatabase

/**
 * The execution context implementation for [SolverSLD]
 *
 * @author Enrico
 */
internal data class ExecutionContextImpl(
        override val libraries: Libraries = Libraries(),
        override val flags: Map<Atom, Term> = emptyMap(),
        override val staticKB: ClauseDatabase = ClauseDatabase.empty(),
        override val dynamicKB: ClauseDatabase = ClauseDatabase.empty(),
        override val substitution: Substitution.Unifier = Substitution.empty(),
        /** The key strategies that a solver should use during resolution process */
        val solverStrategies: SolverStrategies = SolverStrategies.prologStandard,
        /** The side effects manager to be used during resolution process */
        val sideEffectManager: SideEffectManagerImpl = SideEffectManagerImpl()
) : ExecutionContext {

    override val prologStackTrace: Sequence<Struct> by lazy { sideEffectManager.logicalParentRequests.map { it.query } }
}
