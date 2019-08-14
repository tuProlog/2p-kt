package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.solve.solver.statemachine.TimeInstant
import it.unibo.tuprolog.theory.ClauseDatabase

/** A class representing the Solver execution context, containing important information that determines its behaviour */
data class ExecutionContext(
        /** Loaded libraries */
        val libraries: Libraries,
        /** Enabled flags */
        val flags: Map<Atom, Term>,
        /** Static Knowledge-base, that is a KB that *can't* change executing goals */
        val staticKB: ClauseDatabase,
        /** Dynamic Knowledge-base, that is a KB that *can* change executing goals */
        val dynamicKB: ClauseDatabase,
        /** When the overall computation started */
        val computationStartTime: TimeInstant,
        /** The set of actual substitution till this execution context */
        val actualSubstitution: Substitution.Unifier = Substitution.empty(),
        /** The sequence of parent execution contexts before this, till the resolution root */
        val parents: Sequence<ExecutionContext> = emptySequence()
)
