package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.ExecutionContextImpl
import it.unibo.tuprolog.solve.appendPrimitives
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.utils.cursor

internal data class StatePrimitiveSelection(override val context: ExecutionContextImpl) : AbstractState(context) {

    override fun computeNext(): State {
        return with(context) {
            goals.current!!.let { goal ->
                goal.extractSignature().let { signature ->
                    if (libraries.hasPrimitive(signature)) {

                        val req = toRequest(signature, goal.argsList)
                        val primitive = libraries.primitives[signature]
                            ?: error("Inconsistent behaviour of Library.contains and Library.get")

                        try {
                            val primitiveExecutions = primitive(req).cursor() //.also { require(!it.isOver) }


                            val tempExecutionContext = copy(
                                goals = goal.toGoals(),
                                parent = context,
                                depth = nextDepth(),
                                step = nextStep()
                            )

                            val newChoicePointContext =
                                choicePoints.appendPrimitives(primitiveExecutions.next, tempExecutionContext)

                            StatePrimitiveExecution(
                                tempExecutionContext.copy(
                                    primitives = primitiveExecutions,
                                    choicePoints = newChoicePointContext
                                )
                            )
                        } catch (exception: TuPrologRuntimeException) {
                            StateException(
                                exception.updateContext(context), copy(step = nextStep())
                            )
                        }
                    } else {
                        StateRuleSelection(
                            context.copy(step = nextStep())
                        )
                    }
                }
            }
        }
    }

}