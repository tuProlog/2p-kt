package it.unibo.tuprolog.solve.problogclassic.fsm

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.problogclassic.ProblogClassicExecutionContext
import it.unibo.tuprolog.solve.problogclassic.problogAnd

internal data class StateGoalSelection(override val context: ProblogClassicExecutionContext) : AbstractState(context) {
    override fun computeNext(): State {
        return if (context.goals.isOver) {
            if (context.isRoot) {
                StateEnd(
                    Solution.yes(context.query, context.substitution),
                    context.copy(step = nextStep())
                )
            } else {
                val newContext = context.groundProbTermsAndBuildDecisionDiagram()
                StateGoalSelection(
                    with(context.parent!!) {
                        copy(
                            choicePoints = newContext.choicePoints,
                            flags = newContext.flags,
                            dynamicKb = newContext.dynamicKb,
                            staticKb = newContext.staticKb,
                            substitution = newContext.substitution.filter(interestingVariables),
                            goals = goals.next, // go on with parent's goals
                            procedure = procedure,
                            step = nextStep(),
                            startTime = newContext.startTime,
                            operators = newContext.operators,
                            inputChannels = newContext.inputChannels,
                            outputChannels = newContext.outputChannels,
                            libraries = newContext.libraries,
                            bdd = newContext.bdd problogAnd bdd,
                            probRules = newContext.probRules,
                        )
                    }
                )
            }
        } else {
            val goalsWithSubstitution = context.goals.map { it[context.substitution] }

            StatePrimitiveSelection(
                context.groundProbTermsAndBuildDecisionDiagram().copy(
                    goals = goalsWithSubstitution,
                    step = nextStep(),
                )
            )
        }
    }
}
