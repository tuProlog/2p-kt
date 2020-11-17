package it.unibo.tuprolog.solve.probabilistic.fsm

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.probabilistic.ClassicProbabilisticExecutionContext
import it.unibo.tuprolog.utils.Cursor

internal data class StateInit(override val context: ClassicProbabilisticExecutionContext) : AbstractState(context) {

    override fun computeNext(): State {
        return StateGoalSelection(
            context.copy(
                goals = context.query.toGoals().map{context.representationFactory.from(it, Var.anonymous())},
                rules = Cursor.empty(),
                primitives = Cursor.empty(),
                substitution = Substitution.empty(),
                parent = null,
                choicePoints = null,
                depth = 0,
                step = 1
            ).appendRulesAndChoicePoints(Cursor.empty())
        )
    }
}
