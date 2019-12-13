package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.solve.ChoicePointContext
import it.unibo.tuprolog.solve.ExecutionContextImpl
import it.unibo.tuprolog.solve.Solution

internal data class StateBacktracking(override val context: ExecutionContextImpl) : AbstractState(context) {
    override fun computeNext(): State {
        return with(context) {
            if (choicePoints === null || !choicePoints.hasOpenAlternatives) {
                StateEnd(
                    solution = Solution.No(query), context = copy(step = nextStep())
                )
            } else {
                when (val choicePointContext = choicePoints.pathToRoot.first { it.alternatives.hasNext }) {
                    is ChoicePointContext.Rules -> {
                        val tempContext = choicePointContext.executionContext!!.copy(
                            rules = choicePointContext.alternatives,
                            step = nextStep()
                        )

                        val nextChoicePointContext = choicePointContext.copy(
                            alternatives = choicePointContext.alternatives.next,
                            executionContext = tempContext
                        )

                        val nextContext: ExecutionContextImpl = tempContext.copy(choicePoints = nextChoicePointContext)

                        StateRuleExecution(nextContext)
                    }

                    is ChoicePointContext.Primitives -> {
                        val tempContext = choicePointContext.executionContext!!.copy(
                            primitives = choicePointContext.alternatives,
                            step = nextStep()
                        )

                        val nextChoicePointContext = choicePointContext.copy(
                            alternatives = choicePointContext.alternatives.next,
                            executionContext = tempContext
                        )

                        val nextContext: ExecutionContextImpl = tempContext.copy(choicePoints = nextChoicePointContext)

                        StatePrimitiveExecution(nextContext)
                    }
                }
            }
        }
    }

}