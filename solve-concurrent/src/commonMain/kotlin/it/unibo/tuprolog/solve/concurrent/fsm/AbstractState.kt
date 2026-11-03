package it.unibo.tuprolog.solve.concurrent.fsm

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext
import it.unibo.tuprolog.solve.concurrent.stdlib.primitive.Throw
import it.unibo.tuprolog.solve.exception.TimeOutException

/**
 * Common base of every `:solve-concurrent` [State]: wraps [computeNext] with a shared timeout check, so no
 * concrete state has to implement `SolveOptions.timeout` handling itself.
 *
 * [next] first registers one leap on the resolution's [ConcurrentExecutionContext.chronometer] ([isTimeout]), shared
 * by every concurrent branch of the resolution, so their deadline is one and the same; if the budget is known to be
 * exceeded it short-circuits straight to a single-element [EndState] (`StateHalt`) carrying a
 * [TimeOutException], bypassing any pending `catch/3` (resource exhaustion is treated as unrecoverable);
 * otherwise it delegates to [computeNext], the actual per-state transition logic that may return several
 * alternative successor states.
 */
abstract class AbstractState(
    override val context: ConcurrentExecutionContext,
) : State {
    /**
     * Whether the timeout has expired, as established by the chronometer upon this state's leap. Evaluated once,
     * by [next]. States at mandatory refresh points override this to force a clock reading, via
     * [it.unibo.tuprolog.utils.time.Chronometer.checkNow].
     */
    protected open val isTimeout: Boolean
        get() = context.chronometer.leap()

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

    protected fun nextStep(): Long = context.step + 1

    protected fun ConcurrentExecutionContext.skipThrow(): ExecutionContext =
        pathToRoot.first {
            it.procedure?.functor != Throw.functor
        }
}
