package it.unibo.tuprolog.solve.prolog.fsm

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.TimeInstant
import it.unibo.tuprolog.solve.prolog.PrologExecutionContext
import it.unibo.tuprolog.solve.prolog.stdlib.primitive.Throw
import it.unibo.tuprolog.solve.currentTimeInstant
import it.unibo.tuprolog.solve.exception.TimeOutException

/**
 * Common base of every `:solve-prolog` [State]: wraps [computeNext] with a shared timeout check, so no
 * concrete state has to implement `SolveOptions.timeout` handling itself.
 *
 * [next] first compares [executionTime] against [PrologExecutionContext.startTime]/[PrologExecutionContext.maxDuration];
 * if the budget is exceeded it short-circuits straight to an [EndState] (`StateHalt`) carrying a
 * [TimeOutException], bypassing any pending `catch/3` (resource exhaustion is treated as unrecoverable);
 * otherwise it delegates to [computeNext], the actual per-state transition logic.
 */
abstract class AbstractState(
    override val context: PrologExecutionContext,
) : State {
    /** The (lazily captured) instant this state is being evaluated at, used for the timeout check in [next]. */
    protected val executionTime: TimeInstant by lazy {
        currentTime()
    }

    /** Whether [executionTime] is past [PrologExecutionContext.startTime] + [PrologExecutionContext.maxDuration]. */
    protected open val isTimeout: Boolean
        get() = executionTime - context.startTime > context.maxDuration

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

    protected fun currentTime(): TimeInstant = currentTimeInstant()

    protected fun nextStep(): Long = context.step + 1

    protected fun nextDepth(): Int = context.depth + 1

    protected fun previousDepth(): Int = context.depth - 1

    protected fun PrologExecutionContext.skipThrow(): ExecutionContext =
        pathToRoot.first {
            it.procedure?.functor != Throw.functor
        }
}
