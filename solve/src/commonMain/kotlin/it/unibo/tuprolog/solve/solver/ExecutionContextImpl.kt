package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.solve.SideEffectManager
import it.unibo.tuprolog.theory.ClauseDatabase

// TODO doc
data class ExecutionContextImpl(
        /** Loaded libraries */
        override val libraries: Libraries = Libraries(),
        /** Enabled flags */
        override val flags: Map<Atom, Term> = emptyMap(),
        /** Static Knowledge-base, that is a KB that *can't* change executing goals */
        override val staticKB: ClauseDatabase = ClauseDatabase.empty(),
        /** Dynamic Knowledge-base, that is a KB that *can* change executing goals */
        override val dynamicKB: ClauseDatabase = ClauseDatabase.empty(),
        /** The set of current substitution till this execution context */
        override val substitution: Substitution.Unifier = Substitution.empty(),
        /** The key strategies that a solver should use during resolution process */
        override val solverStrategies: SolverStrategies = SolverStrategies.prologStandard,

        // TODO doc
        override val sideEffectManager: SideEffectManager = SideEffectManagerImpl()
) : DeclarativeImplExecutionContext
