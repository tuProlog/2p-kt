package it.unibo.tuprolog.solve.concurrent.fsm

import it.unibo.tuprolog.core.prepareForExecution
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext

data class StateRuleExecution(override val context: ConcurrentExecutionContext) : AbstractState(context) {

    private val failureState: EndState
        get() = StateEnd(
            solution = Solution.no(context.query),
            context = context.copy(step = nextStep())
        )

    override fun computeNext(): Iterable<State> {
        val substitution = with(context) { unificator.mgu(currentGoal!!, rule!!.head) }
        return listOf(
            when {
                substitution.isSuccess -> {
                    val newSubstitution = (context.substitution + substitution).castToUnifier()
                    val subGoals = context.rule!!.prepareForExecution(newSubstitution).body[newSubstitution]
                    StateGoalSelection(
                        context.copy(
                            goals = subGoals.toGoals(),
                            rule = null,
                            substitution = newSubstitution,
                            step = nextStep()
                        )
                    )
                }
                else -> failureState
            }
        )
    }

    override fun clone(context: ConcurrentExecutionContext): StateRuleExecution = copy(context = context)
}
