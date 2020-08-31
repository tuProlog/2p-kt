package it.unibo.tuprolog.solve.solver.fsm.impl.testutils

import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.StreamsSolver
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.solver.SideEffectManagerImpl
import it.unibo.tuprolog.solve.solver.StreamsExecutionContext
import it.unibo.tuprolog.solve.solver.fsm.impl.StateEnd
import it.unibo.tuprolog.solve.solver.fsm.impl.StateGoalEvaluation
import it.unibo.tuprolog.solve.solver.fsm.impl.StateRuleSelection
import it.unibo.tuprolog.solve.stdlib.primitive.Throw
import it.unibo.tuprolog.solve.testutils.SolverTestUtils.createSolveRequest

/**
 * Utils singleton to help testing [StateGoalEvaluation]
 *
 * @author Enrico
 */
internal object StateGoalEvaluationUtils {

    internal val expectedContext = StreamsExecutionContext(sideEffectManager = SideEffectManagerImpl())

    /** Creates a request launching exactly given primitive behaviour */
    internal fun createRequestForPrimitiveResponding(primitiveBehaviour: Primitive): Solve.Request<StreamsExecutionContext> {
        val testPrimitive = PrimitiveWrapper.wrap<StreamsExecutionContext>("testPrimitive", 0) {
            primitiveBehaviour(it)
        }

        return testPrimitive.run {
            createSolveRequest(
                signature withArgs emptyList(),
                primitives = mapOf(descriptionPair, Throw.descriptionPair)
            )
        }
    }

    /** Test data in the form (primitive, list of state types in which it should go) */
    internal val primitiveRequestToNextStateList by lazy {
        mapOf(
            createRequestForPrimitiveResponding { sequenceOf(it.replySuccess(it.context.substitution)) }
                    to listOf(StateEnd.True::class),
            createRequestForPrimitiveResponding { sequenceOf(it.replyFail()) }
                    to listOf(StateEnd.False::class),
            createRequestForPrimitiveResponding { sequenceOf(it.replyWith(true), it.replyWith(false)) }
                    to listOf(StateEnd.True::class, StateEnd.False::class),
            createRequestForPrimitiveResponding { sequenceOf(it.replyException(HaltException(context = it.context))) }
                    to listOf(StateEnd.Halt::class),
            createRequestForPrimitiveResponding { throw HaltException(context = it.context) }
                    to listOf(StateEnd.Halt::class),
            createRequestForPrimitiveResponding {
                sequence {
                    yieldAll(StreamsSolver.solveToResponses(createRequestForPrimitiveResponding { throw HaltException(context = it.context) }))
                }
            } to listOf(StateEnd.Halt::class),
            createRequestForPrimitiveResponding {
                sequence {
                    yieldAll(StreamsSolver.solveToResponses(createRequestForPrimitiveResponding { throw HaltException(context = it.context) }))
                    yield(it.replyFail())
                }
            } to listOf(StateEnd.Halt::class)
        )
    }

    /** Map containing primitive requests that throw [PrologError] instances */
    internal val primitiveRequestThrowingPrologError by lazy {
        listOf(
            createRequestForPrimitiveResponding { throw InstantiationError(context = it.context) },
            createRequestForPrimitiveResponding { throw SystemError(context = it.context) },
            createRequestForPrimitiveResponding {
                throw TypeError(
                    context = it.context,
                    expectedType = TypeError.Expected.CALLABLE,
                    actualValue = Var.of("X")
                )
            }
        )
    }

    /** All requests that should make [StateGoalEvaluation] transit in [StateRuleSelection]  */
    internal val nonPrimitiveRequests by lazy { StateInitUtils.allWellFormedGoalRequests }
}