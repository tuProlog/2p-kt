package it.unibo.tuprolog.solve.concurrent.fsm

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.extractSignature

data class StatePrimitiveSelection(
    override val context: ConcurrentExecutionContext,
) : AbstractState(context) {
    private fun exceptionalState(exception: ResolutionException): Iterable<StateException> =
        listOf(
            StateException(
                exception,
                context.copy(step = nextStep()),
            ),
        )

    override fun computeNext(): Iterable<State> =
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
                            primitive
                                .solve(request)
                                .map { StatePrimitiveExecution(childContext.copy(primitive = it)) }
                                .asIterable()
                        } catch (exception: ResolutionException) {
                            exceptionalState(exception.updateLastContext(childContext.skipThrow()))
                        }
                    } else {
                        listOf(StateRuleSelection(context.copy(step = nextStep())))
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

    override fun clone(context: ConcurrentExecutionContext): StatePrimitiveSelection = copy(context = context)
}
