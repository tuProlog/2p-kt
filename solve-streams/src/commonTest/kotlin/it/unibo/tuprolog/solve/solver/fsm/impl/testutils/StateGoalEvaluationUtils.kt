package it.unibo.tuprolog.solve.solver.fsm.impl.testutils

import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.libraries.stdlib.primitive.Throw
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.SolverSLD
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.prologerror.InstantiationError
import it.unibo.tuprolog.solve.exception.prologerror.SystemError
import it.unibo.tuprolog.solve.exception.prologerror.TypeError
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.SideEffectManagerImpl
import it.unibo.tuprolog.solve.solver.fsm.impl.StateEnd
import it.unibo.tuprolog.solve.solver.fsm.impl.StateGoalEvaluation
import it.unibo.tuprolog.solve.solver.fsm.impl.StateRuleSelection
import it.unibo.tuprolog.solve.testutils.SolverTestUtils.createSolveRequest

/**
 * Utils singleton to help testing [StateGoalEvaluation]
 *
 * @author Enrico
 */
internal object StateGoalEvaluationUtils {

    internal val expectedContext = ExecutionContextImpl(sideEffectManager = SideEffectManagerImpl())

    /** Creates a request launching exactly given primitive behaviour */
    internal fun createRequestForPrimitiveResponding(primitiveBehaviour: Primitive) =
            object : PrimitiveWrapper<ExecutionContext>("testPrimitive", 0) {
                override fun uncheckedImplementation(request: Solve.Request<*>): Sequence<Solve.Response> = primitiveBehaviour(request)
            }.run {
                createSolveRequest(
                        signature withArgs emptyList(),
                        primitives = mapOf(descriptionPair, Throw.descriptionPair)
                )
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
                        yieldAll(SolverSLD.solve(createRequestForPrimitiveResponding { throw HaltException(context = it.context) }))
                    }
                } to listOf(StateEnd.Halt::class),
                createRequestForPrimitiveResponding {
                    sequence {
                        yieldAll(SolverSLD.solve(createRequestForPrimitiveResponding { throw HaltException(context = it.context) }))
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
                createRequestForPrimitiveResponding { throw TypeError(context = it.context, expectedType = TypeError.Expected.CALLABLE, actualValue = Var.of("X")) }
        )
    }

    /** All requests that should make [StateGoalEvaluation] transit in [StateRuleSelection]  */
    internal val nonPrimitiveRequests by lazy { StateInitUtils.allWellFormedGoalRequests }
}