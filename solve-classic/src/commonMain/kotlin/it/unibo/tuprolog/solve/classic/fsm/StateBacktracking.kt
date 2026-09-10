package it.unibo.tuprolog.solve.classic.fsm

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext

/**
 * "Backtracking", the central hub of the state machine: if [ClassicExecutionContext.choicePoints] is empty (or
 * exhausted), moves to `StateEnd` emitting a negative solution; otherwise it walks the choice-point queue's
 * `pathToRoot` for the nearest choice point that still has an alternative, restores that saved execution-context
 * lineage (see [it.unibo.tuprolog.solve.classic.ChoicePointContext.backtrack]), and resumes at either
 * `StatePrimitiveExecution` or `StateRuleExecution`, depending on which kind of alternative was pending.
 */
data class StateBacktracking(
    override val context: ClassicExecutionContext,
) : AbstractState(context) {
    override fun computeNext(): State {
        val choicePoints = context.choicePoints
        return if (choicePoints.let { it === null || !it.hasOpenAlternatives }) {
            StateEnd(
                solution = Solution.no(context.query),
                context = context.copy(step = nextStep()),
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

    override fun clone(context: ClassicExecutionContext): StateBacktracking = copy(context = context)
}
