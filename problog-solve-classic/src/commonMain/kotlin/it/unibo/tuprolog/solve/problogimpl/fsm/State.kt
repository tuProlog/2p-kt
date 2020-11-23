package it.unibo.tuprolog.solve.problogimpl.fsm

import it.unibo.tuprolog.solve.ProblogClassicExecutionContext

interface State {
    val context: ProblogClassicExecutionContext

    fun next(): State
}

fun State.clone(context: ProblogClassicExecutionContext = this.context): State =
    when (this) {
        is StateBacktracking -> copy(context)
        is StateEnd -> copy(solution, context)
        is StateException -> copy(exception, context)
        is StateGoalSelection -> copy(context)
        is StateHalt -> copy(exception, context)
        is StateInit -> copy(context)
        is StatePrimitiveExecution -> copy(context)
        is StatePrimitiveSelection -> copy(context)
        is StateRuleExecution -> copy(context)
        is StateRuleSelection -> copy(context)
        else -> throw IllegalStateException()
    }
