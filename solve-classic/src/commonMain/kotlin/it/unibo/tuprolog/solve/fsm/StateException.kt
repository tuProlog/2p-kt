package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.ExecutionContextImpl
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith
import it.unibo.tuprolog.utils.Cursor

internal data class StateException(
    override val exception: TuPrologRuntimeException,
    override val context: ExecutionContextImpl
) : ExceptionalState, AbstractState(context) {

    override fun computeNext(): State {
        return when (exception) {
            is PrologError -> {
                val catchGoal = context.goals.current!! as Struct

                when {
                    catchGoal.let { it.arity == 3 && it.functor == "catch" } -> {

                        when (val catcher = catchGoal[1].mguWith(exception.errorStruct)) {
                            is Substitution.Unifier -> {
                                val newSubstitution = (context.substitution + catcher) as Substitution.Unifier
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
            is TimeOutException -> StateHalt(
                exception,
                context.copy(step = nextStep())
            )
            else -> StateHalt(exception, context.copy(step = nextStep()))
        }
    }

}