package it.unibo.tuprolog.solve.probabilistic.fsm

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.probabilistic.ClassicProbabilisticExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.solve.probabilistic.representation.ProbabilisticTerm
import it.unibo.tuprolog.utils.Cursor

internal data class StatePrimitiveExecution(override val context: ClassicProbabilisticExecutionContext) : AbstractState(context) {

    private fun ClassicProbabilisticExecutionContext.copyFromCurrentPrimitive(
        goals: Cursor<out ProbabilisticTerm>? = null,
        parentProcedure: Boolean = false,
        substitution: Substitution? = null
    ): ClassicProbabilisticExecutionContext {
        val ctx = primitives.current?.let { apply(it.sideEffects) } ?: this
        return ctx.copy(
            goals = goals ?: this.goals,
            procedure = if (parentProcedure) parent?.procedure else procedure,
            primitives = Cursor.empty(),
            substitution = (substitution ?: this.substitution) as Substitution.Unifier,
            step = nextStep()
        )
    }

    override fun computeNext(): State = try {
        when (val sol = context.primitives.current?.solution) {
            is Solution.Yes -> {
                var newSubstitution = (context.substitution + sol.substitution)
                val curGoalProb = context.goals.current?.toProbability()
                if (curGoalProb is Var) {
                    newSubstitution = newSubstitution.plus(Substitution.of(curGoalProb, Numeric.of(1.0)))
                }
                StateGoalSelection(
                    context.copyFromCurrentPrimitive(
                        goals = context.goals.next,
                        parentProcedure = true,
                        substitution = newSubstitution
                    )
                )
            }
            is Solution.No -> {
                StateBacktracking(context.copyFromCurrentPrimitive())
            }
            is Solution.Halt -> {
                StateException(
                    sol.exception.updateContext(context),
                    context.copyFromCurrentPrimitive()
                )
            }
            null -> {
                StateBacktracking(context.copyFromCurrentPrimitive())
            }
        }
    } catch (exception: TuPrologRuntimeException) {
        StateException(
            exception.updateContext(context),
            context.copy(step = nextStep())
        )
    }
}
