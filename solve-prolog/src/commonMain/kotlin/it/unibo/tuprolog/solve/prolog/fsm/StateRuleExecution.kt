package it.unibo.tuprolog.solve.prolog.fsm

import it.unibo.tuprolog.core.prepareForExecution
import it.unibo.tuprolog.solve.prolog.PrologExecutionContext
import it.unibo.tuprolog.utils.Cursor

/**
 * "Rule Execution": pops the first candidate [it.unibo.tuprolog.core.Rule] from [PrologExecutionContext.rules]
 * and unifies its head with the current goal. On success, the rule's body (with the goal's substitution already
 * applied) becomes the new goal stream and the machine moves back to `StateGoalSelection`; on failure, it moves
 * to `StateBacktracking`.
 */
data class StateRuleExecution(
    override val context: PrologExecutionContext,
) : AbstractState(context) {
    private val failureState: StateBacktracking
        get() = StateBacktracking(context.copy(rules = Cursor.empty(), step = nextStep()))

    override fun computeNext(): State {
        context.countAlternative()
        val selected = context.rules.current!!
        val rule = if (selected is MatchedRule) selected.rule else selected
        val substitution =
            if (selected is MatchedRule) selected.mgu else context.unificator.mgu(context.currentGoal!!, rule.head)
        return when {
            substitution.isSuccess -> {
                val newSubstitution = (context.substitution + substitution).castToUnifier()
                val subGoals =
                    rule
                        .prepareForExecution(newSubstitution)
                        .body[newSubstitution]

                StateGoalSelection(
                    context.copy(
                        goals = subGoals.toGoals(),
                        rules = Cursor.empty(),
                        substitution = newSubstitution,
                        step = nextStep(),
                    ),
                )
            }
            else -> failureState
        }
    }

    override fun clone(context: PrologExecutionContext): StateRuleExecution = copy(context = context)
}
