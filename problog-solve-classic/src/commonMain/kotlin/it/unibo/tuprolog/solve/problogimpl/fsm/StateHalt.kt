package it.unibo.tuprolog.solve.problogimpl.fsm

import it.unibo.tuprolog.solve.ProblogClassicExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException

internal data class StateHalt(
    override val exception: TuPrologRuntimeException,
    override val context: ProblogClassicExecutionContext
) : ExceptionalState, AbstractEndState(
    Solution.Halt(context.query, exception),
    context
)
