package it.unibo.tuprolog.solve.classic.fsm

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.utils.cursor

/**
 * "Primitive Selection": looks up a `Primitive` for the current goal's `(functor, arity)` signature among the
 * loaded libraries. If found, builds a child [ClassicExecutionContext], invokes the primitive, and moves to
 * `StatePrimitiveExecution` with the resulting response cursor already attached as a choice point; if not found,
 * falls through to `StateRuleSelection`. A malformed goal -- an unbound variable, or a term that isn't callable
 * (not a [it.unibo.tuprolog.core.Struct]) -- short-circuits straight to `StateException` here, before any lookup
 * is attempted.
 */
data class StatePrimitiveSelection(
    override val context: ClassicExecutionContext,
) : AbstractState(context) {
    private fun exceptionalState(exception: ResolutionException): StateException =
        StateException(
            exception,
            context.copy(step = nextStep()),
        )

    override fun computeNext(): State =
        with(context) {
            val goal = currentGoal!!
            when {
                goal.isVar -> {
                    exceptionalState(
                        InstantiationError.forGoal(
                            context = context,
                            procedure = context.procedure!!.extractSignature(),
                            variable = goal.castToVar(),
                        ),
                    )
                }
                goal.isStruct -> {
                    val goalStruct = goal.castToStruct()
                    val signature = goalStruct.extractSignature()

                    if (libraries.hasPrimitive(signature)) {
                        val childContext = createChild()
                        try {
                            val request = childContext.toRequest(goalStruct, signature, executionTime)
                            val primitive =
                                libraries.primitives[signature]
                                    ?: error("Inconsistent behaviour of Library.contains and Library.get")
                            val primitiveExecutions = primitive.solve(request).cursor()
                            StatePrimitiveExecution(childContext.appendPrimitivesAndChoicePoints(primitiveExecutions))
                        } catch (exception: ResolutionException) {
                            exceptionalState(exception.updateLastContext(childContext.skipThrow()))
                        }
                    } else {
                        StateRuleSelection(context.copy(step = nextStep()))
                    }
                }
                else -> {
                    exceptionalState(
                        TypeError.forGoal(
                            context = context,
                            procedure = context.procedure?.extractSignature() ?: Signature("?-", 1),
                            expectedType = TypeError.Expected.CALLABLE,
                            culprit = goal,
                        ),
                    )
                }
            }
        }

    override fun clone(context: ClassicExecutionContext): StatePrimitiveSelection = copy(context = context)
}
