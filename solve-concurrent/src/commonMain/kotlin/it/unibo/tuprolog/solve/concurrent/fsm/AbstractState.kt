package it.unibo.tuprolog.solve.concurrent.fsm

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.TimeInstant
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext
import it.unibo.tuprolog.solve.concurrent.stdlib.primitive.Throw
import it.unibo.tuprolog.solve.currentTimeInstant
import it.unibo.tuprolog.solve.exception.TimeOutException

abstract class AbstractState(
    override val context: ConcurrentExecutionContext,
) : State {
    protected val executionTime: TimeInstant by lazy {
        currentTime()
    }

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

    protected abstract fun computeNext(): Iterable<State>

    protected fun currentTime(): TimeInstant = currentTimeInstant()

    protected fun nextStep(): Long = context.step + 1

    protected fun ConcurrentExecutionContext.skipThrow(): ExecutionContext =
        pathToRoot.first {
            it.procedure?.functor != Throw.functor
        }
}
