package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.solve.ClassicExecutionContext

interface State {
    val context: ClassicExecutionContext

    fun next(): State
}

fun State.clone(context: ClassicExecutionContext = this.context): State =
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


