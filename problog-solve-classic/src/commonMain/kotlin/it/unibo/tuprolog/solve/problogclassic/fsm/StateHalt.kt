package it.unibo.tuprolog.solve.problogclassic.fsm

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.solve.problogclassic.ProblogClassicExecutionContext

internal data class StateHalt(
    override val exception: TuPrologRuntimeException,
    override val context: ProblogClassicExecutionContext
) : ExceptionalState, AbstractEndState(
    Solution.halt(context.query, exception),
    context
)
