package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.solve.ExecutionContextImpl
import it.unibo.tuprolog.solve.TimeInstant
import it.unibo.tuprolog.solve.currentTimeInstant
import it.unibo.tuprolog.solve.exception.TimeOutException

internal abstract class AbstractState(override val context: ExecutionContextImpl) : State {

    protected val executionTime: TimeInstant by lazy {
        currentTime()
    }

    private val nextCache: State by lazy {
        val deltaTime = executionTime - context.startTime
        if (deltaTime <= context.maxDuration) {
            computeNext()
        } else {
            StateHalt(
                TimeOutException(
                    exceededDuration = context.maxDuration,
                    context = context
                ),
                context.copy(step = nextStep())
            )
        }
    }

    override fun next(): State = nextCache

    protected abstract fun computeNext(): State

    private fun currentTime(): TimeInstant =
        currentTimeInstant()

    protected fun nextStep(): Long = context.step + 1

    protected fun nextDepth(): Int = context.depth + 1

    protected fun previousDepth(): Int = (context.depth - 1).let { require(it >= 0); it }
}

