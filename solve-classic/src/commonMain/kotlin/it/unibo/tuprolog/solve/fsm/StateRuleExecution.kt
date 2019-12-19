package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.prepareForExecution
import it.unibo.tuprolog.solve.ExecutionContextImpl
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith
import it.unibo.tuprolog.utils.Cursor

internal data class StateRuleExecution(override val context: ExecutionContextImpl) : AbstractState(context) {
    private val failureState: StateBacktracking by lazy {
        StateBacktracking(
            context.copy(rules = Cursor.empty(), step = nextStep())
        )
    }

    override fun computeNext(): State {
        return with(context) {
            when (val unifier = goals.current!! mguWith rules.current!!.head) {
                is Substitution.Unifier -> {
                    val newSubstitution = (substitution + unifier) as Substitution.Unifier
                    val subGoals = rules.current!!.prepareForExecution(newSubstitution).body[newSubstitution]

                    StateGoalSelection(
                        copy(
                            goals = subGoals.toGoals(),
                            rules = Cursor.empty(),
                            substitution = newSubstitution,
                            step = nextStep()
                        )
                    )
                }
                else -> failureState
            }
        }
    }

}