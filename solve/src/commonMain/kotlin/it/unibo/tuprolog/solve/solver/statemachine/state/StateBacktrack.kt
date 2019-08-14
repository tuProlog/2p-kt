package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.solver.statemachine.TimeDuration
import kotlinx.coroutines.CoroutineScope

/**
 * State responsible of backtracking the computation
 *
 * @author Enrico
 */
internal class StateBacktrack(
        override val context: ExecutionContext,
        override val executionScope: CoroutineScope,
        override val executionTimeout: TimeDuration
) : AbstractTimedState(context, executionScope, executionTimeout) {

    override fun behaveTimed(): Sequence<State> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
