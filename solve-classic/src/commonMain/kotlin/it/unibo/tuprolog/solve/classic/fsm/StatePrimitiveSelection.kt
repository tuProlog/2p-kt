package it.unibo.tuprolog.solve.classic.fsm

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.utils.cursor

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
