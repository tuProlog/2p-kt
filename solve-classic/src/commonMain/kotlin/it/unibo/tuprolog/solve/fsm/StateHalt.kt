package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.solve.ClassicExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException

internal data class StateHalt(
    override val exception: TuPrologRuntimeException,
    override val context: ClassicExecutionContext
) : ExceptionalState, AbstractEndState(
    Solution.Halt(context.query, exception),
    context
)
