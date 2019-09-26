package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.solve.Solve
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


        // TODO: should be added a data structure like "ExecutionFlowModifications" containing things related to cut, catch, halt and other flow modifications???
        /** The sequence of parent execution contexts before this, limited to a "clause scope" */
        override val clauseScopedParents: Sequence<ExecutionContextImpl> = emptySequence(),
        /** Valued when this execution context is child of a choicePoint context, indicating a point where to cut */
        override val isChoicePointChild: Boolean = false,
        /** Filled when cut execution happens, this indicates which are the parent contexts whose unexplored children should be cut */
        override val toCutContextsParent: Sequence<ExecutionContextImpl> = emptySequence(),

        // added for catch... should be added as catch specific "ExecutionFlowModification" field?
        /** The sequence of parent [Solve.Request]s from this execution context till the resolution root */
        override val logicalParentRequests: Sequence<Solve.Request<ExecutionContextImpl>> = emptySequence(),

        // added to implement the "throw cut", should go in "ExecutionFlowModification"
        /** The execution context where a `catch` was found and till which other unexplored sub-trees should be cut */
        override val throwRelatedToCutContextsParent: ExecutionContextImpl? = null
) : DeclarativeImplExecutionContext
