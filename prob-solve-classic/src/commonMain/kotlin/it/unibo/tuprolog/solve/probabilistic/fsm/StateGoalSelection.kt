package it.unibo.tuprolog.solve.probabilistic.fsm

import it.unibo.tuprolog.solve.probabilistic.ClassicProbabilisticExecutionContext
import it.unibo.tuprolog.solve.Solution

internal data class StateGoalSelection(override val context: ClassicProbabilisticExecutionContext) : AbstractState(context) {
    override fun computeNext(): State {
        return if (context.goals.isOver) {
            if (context.isRoot) {
                StateEnd(
                    Solution.Yes(context.query, context.substitution),
                    context.copy(step = nextStep())
                )
            } else {
                var newProofConjunction = context.proofConjunction
                val parentGoal = context.parent!!.currentGoal
                val curSubstitution = context.substitution
                parentGoal.let {
                    newProofConjunction = newProofConjunction.plus(
                        context.representationFactory.from(it!![curSubstitution])
                    )
                }
                StateGoalSelection(
                    with(context.parent) {
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
                            libraries = context.libraries,
                            proofConjunction = newProofConjunction
                        )
                    }
                )
            }
        } else {
            val goalsWithSubstitution = context.goals.map {
                it[context.substitution]
            }
            val goalsWithProbability = goalsWithSubstitution.map {
                context.representationFactory.from(it)
            }

            StatePrimitiveSelection(
                context.copy(
                    goals = goalsWithProbability,
                    step = nextStep()
                )
            )
        }
    }
}
