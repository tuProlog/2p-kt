package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve

interface DeclarativeImplExecutionContext : ExecutionContext {

    /** The key strategies that a solver should use during resolution process */
    val solverStrategies: SolverStrategies

    // TODO: 26/09/2019 added here below to preserve compatibility, they should be removed in favour of ExecutionFlowModification field

    // TODO: should be added a data structure like "ExecutionFlowModifications" containing things related to cut, catch, halt and other flow modifications???
    /** The sequence of parent execution contexts before this, limited to a "clause scope" */
    val clauseScopedParents: Sequence<DeclarativeImplExecutionContext>
    /** Valued when this execution context is child of a choicePoint context, indicating a point where to cut */
    val isChoicePointChild: Boolean
    /** Filled when cut execution happens, this indicates which are the parent contexts whose unexplored children should be cut */
    val toCutContextsParent: Sequence<DeclarativeImplExecutionContext>
    // added for catch... should be added as catch specific "ExecutionFlowModification" field?
    /** The sequence of parent [Solve.Request]s from this execution context till the resolution root */
    val logicalParentRequests: Sequence<Solve.Request<DeclarativeImplExecutionContext>>
    // added to implement the "throw cut", should go in "ExecutionFlowModification"
    /** The execution context where a `catch` was found and till which other unexplored sub-trees should be cut */
    val throwRelatedToCutContextsParent: DeclarativeImplExecutionContext?

}
