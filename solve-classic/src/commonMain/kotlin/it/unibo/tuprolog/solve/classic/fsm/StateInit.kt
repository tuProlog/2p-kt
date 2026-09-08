package it.unibo.tuprolog.solve.classic.fsm

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext
import it.unibo.tuprolog.utils.Cursor

/**
 * Bootstrap [State], with no counterpart in the formal paper (whose initial configuration is stipulated rather
 * than computed): resets [context] into a fresh root frame for [ClassicExecutionContext.query] -- goals from the
 * query, empty rule/primitive cursors and choice points, no substitution, `depth = 0` -- and immediately moves
 * to `StateGoalSelection`, the actual entry point of every subsequent step.
 */
data class StateInit(
    override val context: ClassicExecutionContext,
) : AbstractState(context) {
    override fun computeNext(): State =
        StateGoalSelection(
            context
                .copy(
                    goals = context.query.toGoals(),
                    rules = Cursor.empty(),
                    primitives = Cursor.empty(),
                    substitution = Substitution.empty(),
                    parent = null,
                    choicePoints = null,
                    depth = 0,
                    step = 1,
                    customData = context.customData.preservePersistent(),
                    relevantVariables = emptySet(),
                ).appendRulesAndChoicePoints(Cursor.empty()),
        )

    override fun clone(context: ClassicExecutionContext): StateInit = copy(context = context)
}
