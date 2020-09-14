package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.solve.ClassicExecutionContext
import it.unibo.tuprolog.solve.TimeInstant
import it.unibo.tuprolog.solve.currentTimeInstant
import it.unibo.tuprolog.solve.exception.TimeOutException

internal abstract class AbstractState(override val context: ClassicExecutionContext) : State {

    protected val executionTime: TimeInstant by lazy {
        currentTime()
    }

    protected open val isTimeout: Boolean
        get() = executionTime - context.startTime > context.maxDuration

    override fun next(): State {
        return if (isTimeout) {
            StateHalt(
                TimeOutException(
                    exceededDuration = context.maxDuration,
                    context = context
                ),
                context.copy(step = nextStep())
            )
        } else {
            computeNext()
        }
    }

    protected abstract fun computeNext(): State

    protected fun currentTime(): TimeInstant =
        currentTimeInstant()

    protected fun nextStep(): Long = context.step + 1

    protected fun nextDepth(): Int = context.depth + 1

    protected fun previousDepth(): Int = context.depth - 1
}
