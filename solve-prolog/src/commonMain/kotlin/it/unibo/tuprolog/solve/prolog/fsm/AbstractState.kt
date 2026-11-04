package it.unibo.tuprolog.solve.prolog.fsm

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.prolog.PrologExecutionContext
import it.unibo.tuprolog.solve.prolog.stdlib.primitive.Throw

/**
 * Common base of every `:solve-prolog` [State]: wraps [computeNext] with a shared timeout check, so no
 * concrete state has to implement `SolveOptions.timeout` handling itself.
 *
 * [next] first registers one leap on the resolution's [PrologExecutionContext.chronometer] ([isTimeout]), which
 * may or may not read the clock (see `it.unibo.tuprolog.utils.time.ChronometerFactory`); if the budget is known to
 * be exceeded it short-circuits straight to an [EndState] (`StateHalt`) carrying a
 * [TimeOutException], bypassing any pending `catch/3` (resource exhaustion is treated as unrecoverable);
 * otherwise it delegates to [computeNext], the actual per-state transition logic.
 */
abstract class AbstractState(
    override val context: PrologExecutionContext,
) : State {
    /**
     * Whether the timeout has expired, as established by the chronometer upon this state's leap. Evaluated once,
     * by [next]. States at mandatory refresh points override this to force a clock reading, via
     * [it.unibo.tuprolog.utils.time.Chronometer.checkNow].
     */
    protected open val isTimeout: Boolean
        get() = context.chronometer.leap()

    override fun next(): State =
        if (isTimeout) {
            StateHalt(
                TimeOutException(
                    exceededDuration = context.maxDuration,
                    context = context,
                ),
                context.copy(step = nextStep()),
            )
        } else {
            computeNext()
        }

    /** The actual, state-specific transition logic, invoked by [next] once the timeout check has passed. */
    protected abstract fun computeNext(): State

    protected fun nextStep(): Long = context.step + 1

    protected fun nextDepth(): Int = context.depth + 1

    protected fun previousDepth(): Int = context.depth - 1

    protected fun PrologExecutionContext.skipThrow(): ExecutionContext =
        pathToRoot.first {
            it.procedure?.functor != Throw.functor
        }
}
