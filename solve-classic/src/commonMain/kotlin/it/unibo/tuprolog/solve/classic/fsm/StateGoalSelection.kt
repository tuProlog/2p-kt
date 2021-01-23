package it.unibo.tuprolog.solve.classic.fsm

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext

internal data class StateGoalSelection(override val context: ClassicExecutionContext) : AbstractState(context) {
    override fun computeNext(): State {
        return if (context.goals.isOver) {
            if (context.isRoot) {
                StateEnd(
                    Solution.yes(context.query, context.substitution),
                    context.copy(step = nextStep())
                )
            } else {
                StateGoalSelection(
                    with(context.parent!!) {
                        copy(
                            choicePoints = context.choicePoints,
                            flags = context.flags,
                            dynamicKb = context.dynamicKb,
                            staticKb = context.staticKb,
                            substitution = context.substitution.filter(interestingVariables),
                            goals = goals.next, // go on with parent's goals
                            procedure = procedure,
                            step = nextStep(),
                            startTime = context.startTime,
                            operators = context.operators,
                            inputChannels = context.inputChannels,
                            outputChannels = context.outputChannels,
                            libraries = context.libraries
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
