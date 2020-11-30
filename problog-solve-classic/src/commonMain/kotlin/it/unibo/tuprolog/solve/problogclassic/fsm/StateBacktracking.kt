package it.unibo.tuprolog.solve.problogclassic.fsm

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.problogclassic.ProblogClassicExecutionContext

internal data class StateBacktracking(override val context: ProblogClassicExecutionContext) : AbstractState(context) {
    override fun computeNext(): State {
        val choicePoints = context.choicePoints
        return if (choicePoints.let { it === null || !it.hasOpenAlternatives }) {
            StateEnd(
                solution = Solution.No(context.query),
                context = context.copy(step = nextStep())
            )
        } else {
            val choicePointContext = choicePoints!!.pathToRoot.first { it.alternatives.hasNext }
            val nextContext = choicePointContext.backtrack(context)
            if (nextContext.primitives.hasNext) {
                StatePrimitiveExecution(nextContext)
            } else {
                StateRuleExecution(nextContext)
            }
        }
    }
}
