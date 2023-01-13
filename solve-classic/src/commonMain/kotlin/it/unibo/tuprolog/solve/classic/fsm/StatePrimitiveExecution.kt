package it.unibo.tuprolog.solve.classic.fsm

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext
import it.unibo.tuprolog.solve.exception.ResolutionException
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
        context.primitives.current?.solution?.whenIs(
            yes = {
                StateGoalSelection(
                    context.copyFromCurrentPrimitive(
                        goals = context.goals.next,
                        procedureFromAncestor = 1,
                        substitution = context.unificator.merge(
                            context.substitution,
                            it.substitution,
                            occurCheckEnabled = false
                        )
                    )
                )
            },
            no = {
                StateBacktracking(context.parent!!.copyFromCurrentPrimitive())
            },
            halt = {
                StateException(it.exception.updateLastContext(context.skipThrow()), context.copyFromCurrentPrimitive())
            },
            otherwise = { throw IllegalStateException("This should never happen") }
        ) ?: StateBacktracking(context.copyFromCurrentPrimitive())
    } catch (exception: ResolutionException) {
        StateException(exception.updateLastContext(context.skipThrow()), context.copy(step = nextStep()))
    }

    override fun clone(context: ClassicExecutionContext): StatePrimitiveExecution = copy(context = context)
}
