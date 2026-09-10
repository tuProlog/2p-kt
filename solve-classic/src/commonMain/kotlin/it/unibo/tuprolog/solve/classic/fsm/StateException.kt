package it.unibo.tuprolog.solve.classic.fsm

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext
import it.unibo.tuprolog.solve.classic.stdlib.rule.Catch
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.exception.error.MessageError
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.Cursor
import it.unibo.tuprolog.utils.plus

/**
 * "Exception": reached whenever a primitive's response, or an ISO error raised mid-resolution, carries an
 * exception rather than a substitution. Climbs the execution-context stack (`context.parent`, one frame at a
 * time, mirroring the paper's "search for a `catch/3` frame") looking for a currently-executing
 * `catch(Goal, Catcher, Recovery)` whose `Catcher` unifies with the exception's content. If found, the recovery
 * goal becomes the new goal stream and the machine resumes at `StateGoalSelection`; if the root context is
 * reached without a match, it moves to `StateHalt` with the exception attached to the emitted solution (an
 * internal [it.unibo.tuprolog.solve.exception.error.MessageError] is converted to a public
 * [it.unibo.tuprolog.solve.exception.error.SystemError] at that point).
 */
data class StateException(
    override val exception: ResolutionException,
    override val context: ClassicExecutionContext,
) : AbstractState(context),
    ExceptionalState {
    private fun Struct.isCatch(): Boolean = arity == 3 && functor == Catch.functor

    private fun LogicError.getExceptionContent(): Term =
        when (this) {
            is MessageError -> content
            else -> errorStruct
        }

    private fun ResolutionException.toPublicException(): ResolutionException =
        when (this) {
            is MessageError -> SystemError.forUncaughtError(this)
            else -> this
        }

    private val finalState: EndState
        get() =
            StateHalt(
                exception.toPublicException(),
                context.copy(step = nextStep()),
            )

    private val handleExceptionInParentContext: StateException
        get() =
            StateException(
                exception,
                context.parent!!.copy(step = nextStep()),
            )

    private fun handleStruct(
        unificator: Unificator,
        catchGoal: Struct,
        error: LogicError,
    ): State =
        when {
            catchGoal.isCatch() -> {
                val catcher = unificator.mgu(catchGoal[1], error.getExceptionContent())
                handleCatch(catchGoal, catcher)
            }
            context.isRoot -> finalState
            else -> handleExceptionInParentContext
        }

    private fun handleCatch(
        catchGoal: Struct,
        catcher: Substitution,
    ) = when {
        catcher.isSuccess -> {
            val newSubstitution =
                (context.substitution + catcher).filter { (it, _) ->
                    context.isVariableInteresting(it)
                }
            val subGoals = catchGoal[2][newSubstitution]
            val newGoals = subGoals.toGoals() + context.goals.next

            StateGoalSelection(
                context.copy(
                    goals = newGoals,
                    rules = Cursor.empty(),
                    primitives = Cursor.empty(),
                    substitution = newSubstitution.castToUnifier(),
                    step = nextStep(),
                ),
            )
        }
        context.isRoot -> finalState
        else -> handleExceptionInParentContext
    }

    override fun computeNext(): State =
        when (exception) {
            is LogicError -> {
                val catchGoal = context.currentGoal!!
                when {
                    catchGoal.isStruct -> handleStruct(context.unificator, catchGoal.castToStruct(), exception)
                    context.isRoot -> finalState
                    else -> handleExceptionInParentContext
                }
            }
            else -> finalState
        }

    override fun clone(context: ClassicExecutionContext): StateException =
        copy(
            exception = exception,
            context = context,
        )
}
