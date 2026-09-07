package it.unibo.tuprolog.solve.concurrent.fsm

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.TimeInstant
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext
import it.unibo.tuprolog.solve.concurrent.stdlib.primitive.Throw
import it.unibo.tuprolog.solve.currentTimeInstant
import it.unibo.tuprolog.solve.exception.TimeOutException

/**
 * Common base of every `:solve-concurrent` [State]: wraps [computeNext] with a shared timeout check, so no
 * concrete state has to implement `SolveOptions.timeout` handling itself.
 *
 * [next] first compares [executionTime] against [ConcurrentExecutionContext.startTime]/[ConcurrentExecutionContext.maxDuration];
 * if the budget is exceeded it short-circuits straight to a single-element [EndState] (`StateHalt`) carrying a
 * [TimeOutException], bypassing any pending `catch/3` (resource exhaustion is treated as unrecoverable);
 * otherwise it delegates to [computeNext], the actual per-state transition logic that may return several
 * alternative successor states.
 */
abstract class AbstractState(
    override val context: ConcurrentExecutionContext,
) : State {
    /** The (lazily captured) instant this state is being evaluated at, used for the timeout check in [next]. */
    protected val executionTime: TimeInstant by lazy {
        currentTime()
    }

    /** Whether [executionTime] is past [ConcurrentExecutionContext.startTime] + [ConcurrentExecutionContext.maxDuration]. */
    protected open val isTimeout: Boolean
        get() = executionTime - context.startTime > context.maxDuration

    override fun next(): Iterable<State> =
        if (isTimeout) {
            listOf(
                StateHalt(
                    TimeOutException(
                        exceededDuration = context.maxDuration,
                        context = context,
                    ),
                    context.copy(step = nextStep()),
                ),
            )
        } else {
            computeNext()
        }

    /** The actual, state-specific transition logic, invoked by [next] once the timeout check has passed. */
    protected abstract fun computeNext(): Iterable<State>

    protected fun currentTime(): TimeInstant = currentTimeInstant()

    protected fun nextStep(): Long = context.step + 1

    protected fun ConcurrentExecutionContext.skipThrow(): ExecutionContext =
        pathToRoot.first {
            it.procedure?.functor != Throw.functor
        }
}
