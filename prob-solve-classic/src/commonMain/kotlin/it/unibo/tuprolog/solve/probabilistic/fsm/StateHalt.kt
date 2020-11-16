package it.unibo.tuprolog.solve.probabilistic.fsm

import it.unibo.tuprolog.solve.probabilistic.ClassicExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException

internal data class StateHalt(
    override val exception: TuPrologRuntimeException,
    override val context: ClassicExecutionContext
) : ExceptionalState, AbstractEndState(
    Solution.Halt(context.query, exception),
    context
)
