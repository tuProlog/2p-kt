package it.unibo.tuprolog.solve.classic.fsm

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.utils.Cursor

data class StatePrimitiveExecution(override val context: ClassicExecutionContext) : AbstractState(context) {

    private fun ClassicExecutionContext.copyFromCurrentPrimitive(
        goals: Cursor<out Term>? = null,
        procedureFromAncestor: Int = 0,
        substitution: Substitution? = null
    ): ClassicExecutionContext {
        val ctx = primitives.current?.let { apply(it.sideEffects) } ?: this
        return ctx.copy(
            goals = goals ?: this.goals,
            procedure = pathToRoot.drop(procedureFromAncestor).map { it.procedure }.first(),
            primitives = Cursor.empty(),
            substitution = (substitution ?: this.substitution) as Substitution.Unifier,
            step = nextStep()
        )
    }

    override fun computeNext(): State = try {
        when (val sol = context.primitives.current?.solution) {
            is Solution.Yes -> {
                StateGoalSelection(
                    context.copyFromCurrentPrimitive(
                        goals = context.goals.next,
                        procedureFromAncestor = 1,
                        substitution = (context.substitution + sol.substitution)
                    )
                )
            }
            is Solution.No -> {
                StateBacktracking(context.parent!!.copyFromCurrentPrimitive())
            }
            is Solution.Halt -> {
                StateException(sol.exception.updateLastContext(context), context.copyFromCurrentPrimitive())
            }
            null -> {
                StateBacktracking(context.copyFromCurrentPrimitive())
            }
            else -> {
                throw IllegalStateException("This should never happen")
            }
        }
    } catch (exception: TuPrologRuntimeException) {
        StateException(exception.updateLastContext(context), context.copy(step = nextStep()))
    }

    override fun clone(context: ClassicExecutionContext): StatePrimitiveExecution = copy(context = context)
}
