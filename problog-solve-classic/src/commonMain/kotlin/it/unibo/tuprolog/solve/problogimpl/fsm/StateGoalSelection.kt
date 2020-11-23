package it.unibo.tuprolog.solve.problogimpl.fsm

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.ProblogClassicExecutionContext
import it.unibo.tuprolog.solve.problogimpl.ProblogFact
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.problogimpl.stdlib.DefaultBuiltins
import it.unibo.tuprolog.struct.BinaryDecisionDiagram
import it.unibo.tuprolog.struct.impl.ofVar

internal data class StateGoalSelection(override val context: ProblogClassicExecutionContext) : AbstractState(context) {
    override fun computeNext(): State {
        return if (context.goals.isOver) {
            if (context.isRoot) {
                StateEnd(
                    Solution.Yes(context.query, context.substitution),
                    context.copy(step = nextStep())
                )
            } else {
                /* Apply a substitution to the most recent Probabilistic Rule/Fact */
                var curBDD = context.bdd
                val curProbRule = context.probRule
                if(curProbRule != null) {
                    val factWithSubstitution = curProbRule[context.substitution]

                    /* Map the result in a Probabilisitc Fact and rebuild the BDD */
                    if (factWithSubstitution is Rule) {
                        val newProbFact = ProblogFact(
                            curProbRule.id,
                            curProbRule.probability,
                            factWithSubstitution.head
                        )
                        curBDD = curBDD and BinaryDecisionDiagram.ofVar(newProbFact)
                    }
                }
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
                            libraries = context.libraries,
                            bdd = bdd and curBDD
                        )
                    }
                )
            }
        } else {
            val goalsWithSubstitution = context.goals.map { it[context.substitution] }

            StatePrimitiveSelection(
                context.copy(
                    goals = goalsWithSubstitution,
                    step = nextStep(),
                )
            )
        }
    }
}
