package it.unibo.tuprolog.solve.concurrent.fsm

import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.extractSignature

data class StateRuleSelection(override val context: ConcurrentExecutionContext) : State {

    private fun exceptionalState(exception: ResolutionException): StateException =
        StateException(exception, context.copy(step = nextStep()))

    private val ignoreState: StateGoalSelection
        get() = StateGoalSelection(context.copy(goals = context.goals.next, step = nextStep()))

    override fun next(): Iterable<State> {
        val currentGoal = context.currentGoal!!
        return listOf<State>( when {
            currentGoal.isVar ->
                exceptionalState(InstantiationError.forGoal(
                    context = context,
                    procedure = context.procedure!!.extractSignature(),
                    variable = currentGoal.castToVar()
                ))
            currentGoal.isStruct -> with(context) {
                val currentGoalStruct = currentGoal.castToStruct()
                val ruleSources = sequenceOf(libraries.theory, staticKb, dynamicKb)
                when {
                    currentGoalStruct.isTruth -> {
                        if (currentGoalStruct.isTrue) {
                            ignoreState
                        }
                        /*
                        todo there is no backtracking in concurrent return EndState(Solution.NO)
                         */
                    }
                }
                StateGoalSelection(context.copy(goals = context.goals.next, step = nextStep()))
            }
            else -> exceptionalState(
                TypeError.forGoal(
                    context = context,
                    procedure = context.procedure!!.extractSignature(),
                    expectedType = TypeError.Expected.CALLABLE,
                    culprit = currentGoal))
        })
    }

    override fun clone(context: ConcurrentExecutionContext): State = copy(context = context)
}
