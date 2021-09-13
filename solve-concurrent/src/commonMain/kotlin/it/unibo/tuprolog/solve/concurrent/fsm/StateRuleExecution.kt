package it.unibo.tuprolog.solve.concurrent.fsm

import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext

data class StateRuleExecution(override val context: ConcurrentExecutionContext) : State {
    override fun next(): Iterable<State> {
        TODO("Not yet implemented")
    }

    override fun clone(context: ConcurrentExecutionContext): State = copy(context = context)
}
