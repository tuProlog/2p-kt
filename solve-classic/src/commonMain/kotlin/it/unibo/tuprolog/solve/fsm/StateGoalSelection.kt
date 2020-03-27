package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.solve.ExecutionContextImpl
import it.unibo.tuprolog.solve.Solution

internal data class StateGoalSelection(override val context: ExecutionContextImpl) : AbstractState(context) {
    override fun computeNext(): State {

        return if (context.goals.isOver) {
            if (context.isRoot) {
                StateEnd(
                    Solution.Yes(context.query, context.substitution),
                    context.copy(step = nextStep())
                )
            } else {
                StateGoalSelection(
                    with(context.parent!!) {
                        copy(
                            choicePoints = context.choicePoints,
                            flags = context.flags,
                            dynamicKB = context.dynamicKB,
                            staticKB = context.staticKB,
                            substitution = context.substitution.filter(interestingVariables),
                            goals = goals.next, // go on with parent's goals
                            procedure = procedure,
                            step = nextStep()
                        )
                    }
                )
            }
        } else {
            val goalsWithSubstitution = context.goals.map { it[context.substitution] }

            StatePrimitiveSelection(
                context.copy(
                    goals = goalsWithSubstitution,
                    step = nextStep()
                )
            )
        }
    }
}