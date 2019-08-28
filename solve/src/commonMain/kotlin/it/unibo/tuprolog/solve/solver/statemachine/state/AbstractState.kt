package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.statemachine.StateMachineExecutor
import kotlinx.coroutines.CoroutineScope

/**
 * Base class for all States refactoring common behaviour
 *
 * @author Enrico
 */
abstract class AbstractState(
        override val solveRequest: Solve.Request,
        /** The execution scope where state behavior should execute computationally */
        protected open val executionStrategy: CoroutineScope
) : State {
    override val hasBehaved = false

    /** The execute function to be used when a [State] needs, internally, to execute sub-[State]s behaviour */
    protected open val subStateExecute: (State) -> Sequence<AlreadyExecutedState> = StateMachineExecutor::executeWrapping
}
