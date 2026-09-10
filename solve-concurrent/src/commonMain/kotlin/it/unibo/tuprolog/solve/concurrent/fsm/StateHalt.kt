package it.unibo.tuprolog.solve.concurrent.fsm

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext
import it.unibo.tuprolog.solve.exception.ResolutionException

/**
 * The terminal, unrecoverable [EndState] a branch reaches when [exception] could not be handled by any enclosing
 * `catch/3` (or was a timeout, see [AbstractState]): [solution] is a [Solution.Halt]. Reaching a [StateHalt] does
 * not, by itself, stop sibling branches of the search tree -- it is up to the coroutine driving this branch (and,
 * ultimately, whoever consumes the resolution's solution channel) to decide whether a halted branch should abort
 * the whole resolution.
 */
data class StateHalt(
    override val exception: ResolutionException,
    override val context: ConcurrentExecutionContext,
) : AbstractState(context),
    ExceptionalState,
    EndState {
    override val solution: Solution = Solution.halt(context.query, exception)

    override fun next(): Iterable<State> = super<AbstractState>.next()

    override fun computeNext(): Iterable<State> = throw NoSuchElementException()

    override fun clone(context: ConcurrentExecutionContext): StateHalt = copy(exception = exception, context = context)
}
