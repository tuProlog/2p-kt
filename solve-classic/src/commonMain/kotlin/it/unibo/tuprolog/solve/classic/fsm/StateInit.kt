package it.unibo.tuprolog.solve.classic.fsm

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext
import it.unibo.tuprolog.utils.Cursor

data class StateInit(override val context: ClassicExecutionContext) : AbstractState(context) {
    override fun computeNext(): State {
        return StateGoalSelection(
            context.copy(
                goals = context.query.toGoals(),
                rules = Cursor.empty(),
                primitives = Cursor.empty(),
                substitution = Substitution.empty(),
                parent = null,
                choicePoints = null,
                depth = 0,
                step = 1,
                customData = context.customData.preservePersistent(),
                relevantVariables = emptySet()
            ).appendRulesAndChoicePoints(Cursor.empty())
        )
    }

    override fun clone(context: ClassicExecutionContext): StateInit = copy(context = context)
}
