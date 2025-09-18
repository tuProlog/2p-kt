package it.unibo.tuprolog.solve.concurrent.fsm

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.utils.Cursor

data class StatePrimitiveExecution(
    override val context: ConcurrentExecutionContext,
) : AbstractState(context) {
    private fun failureState(context: ConcurrentExecutionContext = this.context): EndState =
        StateEnd(
            solution = Solution.no(context.query),
            context = context.copy(step = nextStep()),
        )

    private fun ConcurrentExecutionContext.copyFromCurrentPrimitive(
        goals: Cursor<out Term>? = null,
        procedureFromAncestor: Int = 0,
        substitution: Substitution? = null,
    ): ConcurrentExecutionContext {
        val ctx = primitive?.let { apply(it.sideEffects) } ?: this
        return ctx.copy(
            goals = goals ?: this.goals,
            procedure = pathToRoot.drop(procedureFromAncestor).map { it.procedure }.first(),
            primitive = null,
            substitution = (substitution ?: this.substitution) as Substitution.Unifier,
            step = nextStep(),
        )
    }

    override fun computeNext(): Iterable<State> =
        listOf(
            try {
                context.primitive?.solution?.whenIs(
                    yes = {
                        StateGoalSelection(
                            context.copyFromCurrentPrimitive(
                                goals = context.goals.next,
                                procedureFromAncestor = 1,
                                substitution = (context.substitution + it.substitution),
                            ),
                        )
                    },
                    no = {
                        failureState(context.parent!!.copyFromCurrentPrimitive())
                    },
                    halt = {
                        StateException(
                            it.exception.updateLastContext(context.skipThrow()),
                            context.copyFromCurrentPrimitive(),
                        )
                    },
                    otherwise = { throw IllegalStateException("This should never happen") },
                ) ?: failureState(context.parent!!.copyFromCurrentPrimitive())
            } catch (exception: ResolutionException) {
                StateException(exception.updateLastContext(context.skipThrow()), context.copy(step = nextStep()))
            },
        )

    override fun clone(context: ConcurrentExecutionContext): StatePrimitiveExecution = copy(context = context)
}
