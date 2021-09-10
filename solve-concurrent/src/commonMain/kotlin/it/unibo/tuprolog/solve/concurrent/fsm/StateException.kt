package it.unibo.tuprolog.solve.concurrent.fsm

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.exception.error.MessageError
import it.unibo.tuprolog.solve.exception.error.SystemError

data class StateException(
    override val exception: ResolutionException,
    override val context: ConcurrentExecutionContext
    ): ExceptionalState {

    private fun ResolutionException.toPublicException(): ResolutionException =
        when (this) {
            is MessageError -> SystemError.forUncaughtError(this)
            else -> this
        }

    private val finalState: EndState
        get() = StateHalt(
            exception.toPublicException(),
            context.copy(step = context.step + 1)
        )

    private val handleExceptionInParentContext: StateException
        get() = StateException(
            exception,
            context.parent!!.copy(step = context.step + 1)
        )

    private fun handleStruct(catchGoal: Struct, error: LogicError): State =
        when {
            // todo catchGoal.isCatch() -> {similar to classic}
            context.isRoot -> finalState
            else -> handleExceptionInParentContext
        }

    /* todo
    private fun handleCatch(catchGoal: Struct, catcher: Substitution) =
        when {
            catcher.isSuccess -> {
                val newSubstitution =
                    (context.substitution + catcher).filter(context.interestingVariables)
                val subGoals = catchGoal[2][newSubstitution]
                val newGoals = subGoals.toGoals() + context.goals.next

                StateGoalSelection(
                    context.copy(
                        goals = newGoals,
                        rules = Cursor.empty(),
                        primitives = Cursor.empty(),
                        substitution = newSubstitution.castToUnifier(),
                        step = nextStep()
                    )
                )
            }
            context.isRoot -> finalState
            else -> handleExceptionInParentContext
        }
    */

    override fun next(): Iterable<State> {
        return listOf( when (exception) {
            is LogicError -> {
                val catchGoal = context.currentGoal!!
                when {
                    catchGoal.isStruct -> handleStruct(catchGoal.castToStruct(), exception)
                    context.isRoot -> finalState
                    else -> handleExceptionInParentContext
                }
            }
            else -> finalState
        })
    }

    override fun clone(context: ConcurrentExecutionContext): State = copy(exception = exception, context = context)
}
