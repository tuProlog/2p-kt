package it.unibo.tuprolog.solve.classic.fsm

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException

internal data class StateHalt(
    override val exception: TuPrologRuntimeException,
    override val context: ClassicExecutionContext
) : ExceptionalState, AbstractEndState(Solution.halt(context.query, exception), context) {
    override fun clone(context: ClassicExecutionContext): StateHalt = copy(exception = exception, context = context)
}
