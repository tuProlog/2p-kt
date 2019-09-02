package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import kotlinx.coroutines.CoroutineScope

/**
 * Initial state that should Initialize the state-machine
 *
 * @author Enrico
 */
internal class StateInit(
        override val solveRequest: Solve.Request,
        override val executionStrategy: CoroutineScope
) : AbstractTimedState(solveRequest, executionStrategy) {

    override fun behaveTimed(): Sequence<State> = sequence {
        yield(
                StateGoalSelection(
                        solveRequest.copy(context = initializationWork(solveRequest.context)),
                        executionStrategy
                )
        )
    }

    /** Any state machine initialization should be done here */
    private fun initializationWork(context: ExecutionContext): ExecutionContext {
        return context.copy(
                clauseScopedParents = sequence { yield(context); yieldAll(context.clauseScopedParents) },
                isChoicePointChild = false
        )
    }
}
