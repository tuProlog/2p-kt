package it.unibo.tuprolog.solve.concurrent.fsm

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext
import it.unibo.tuprolog.solve.exception.ResolutionException

data class StateHalt(
    override val exception: ResolutionException,
    override val context: ConcurrentExecutionContext
) : ExceptionalState, EndState {

    override val solution: Solution = Solution.halt(context.query, exception)

    override fun next(): Iterable<State> = throw NoSuchElementException()

    override fun clone(context: ConcurrentExecutionContext): State = copy(exception = exception, context = context)
}
