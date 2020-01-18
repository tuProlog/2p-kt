package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.libraries.stdlib.rule.Catch
import it.unibo.tuprolog.solve.ExecutionContextImpl
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.solve.exception.prologerror.MessageError
import it.unibo.tuprolog.solve.exception.prologerror.SystemError
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith
import it.unibo.tuprolog.utils.Cursor

internal data class StateException(
    override val exception: TuPrologRuntimeException,
    override val context: ExecutionContextImpl
) : ExceptionalState, AbstractState(context) {

    private fun Struct.isCatch(): Boolean =
        arity == 3 && functor == Catch.functor

    private fun PrologError.getExceptionContent(): Term {
        return when (this) {
            is MessageError -> content
            else -> errorStruct
        }
    }

    private fun PrologError.toPublicException(): PrologError =
        when (this) {
            is MessageError -> SystemError.forUncaughtException(this@StateException.context, this)
            else -> this
        }

    override fun computeNext(): State {
        return when (exception) {
            is PrologError -> {
                val catchGoal = context.currentGoal!!

                when {
                    catchGoal !is Struct -> {
                        StateHalt(exception, context.copy(step = nextStep()))
                    }
                    catchGoal.isCatch() -> {
                        val catcher = catchGoal[1].freshCopy() mguWith exception.getExceptionContent()

                        when {
                            catcher is Substitution.Unifier -> {
                                val newSubstitution = (context.substitution + catcher).filter(context.interestingVariables) as Substitution.Unifier
                                val subGoals = catchGoal[2][newSubstitution]

                                StateGoalSelection(
                                    context.copy(
                                        goals = subGoals.toGoals(),
                                        rules = Cursor.empty(),
                                        primitives = Cursor.empty(),
                                        substitution = newSubstitution,
                                        step = nextStep()
                                    )
                                )
                            }
                            context.isRoot -> {
                                StateHalt(
                                    exception.toPublicException(),
                                    context.copy(step = nextStep())
                                )
                            }
                            else -> {
                                StateException(
                                    exception,
                                    context.parent!!.copy(step = nextStep())
                                )
                            }
                        }
                    }
                    context.isRoot -> {
                        StateHalt(
                            exception,
                            context.copy(step = nextStep())
                        )
                    }
                    else -> {
                        StateException(
                            exception,
                            context.parent!!.copy(step = nextStep())
                        )
                    }
                }
            }
            is TimeOutException -> {
                StateHalt(
                    exception,
                    context.copy(step = nextStep())
                )
            }
            else -> {
                StateHalt(exception, context.copy(step = nextStep()))
            }
        }
    }

}