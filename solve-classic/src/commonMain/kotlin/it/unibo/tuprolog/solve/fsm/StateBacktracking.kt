package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.solve.ClassicExecutionContext
import it.unibo.tuprolog.solve.Solution

internal data class StateBacktracking(override val context: ClassicExecutionContext) : AbstractState(context) {
    override fun computeNext(): State {
        return with(context) {
            if (choicePoints === null || !choicePoints.hasOpenAlternatives) {
                StateEnd(
                    solution = Solution.No(query),
                    context = copy(step = nextStep())
                )
            } else {
                val choicePointContext = choicePoints.pathToRoot.first { it.alternatives.hasNext }
                StateRuleExecution(choicePointContext.backtrack(nextStep()))
            }
        }
    }

}