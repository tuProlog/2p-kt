package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.solve.solver.SolverStrategies
import it.unibo.tuprolog.solve.solver.statemachine.TimeInstant
import it.unibo.tuprolog.solve.solver.statemachine.currentTime
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
        val computationStartTime: TimeInstant = currentTime(),
        /** The set of current substitution till this execution context */
        val currentSubstitution: Substitution.Unifier = Substitution.empty(),
        /** The sequence of parent execution contexts before this, for "rule scope" */
        val parents: Sequence<ExecutionContext> = emptySequence(),
        /** The key strategies that a solver should use during resolution process */
        val solverStrategies: SolverStrategies = SolverStrategies.prologStandard,


        // TODO: should be added a data structure like "ExecutionFlowModifications" containing things related to cut, catch, halt and other flow modifications???
        /** Valued when this execution context is child of a choicePoint context, indicating a point where to cut */
        val isChoicePointChild: Boolean = false,
        /** Filled when cut execution happens, this indicates which are the parent contexts whose unexplored children should be cut */
        val toCutContextsParent: Sequence<ExecutionContext> = emptySequence()
)
