package it.unibo.tuprolog.solve.solver.statemachine.state.testutils

import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.PrimitiveWrapper
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.prologerror.InstantiationError
import it.unibo.tuprolog.solve.exception.prologerror.SystemError
import it.unibo.tuprolog.solve.exception.prologerror.TypeError
import it.unibo.tuprolog.libraries.stdlib.Throw
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.SideEffectManagerImpl
import it.unibo.tuprolog.solve.solver.SolverSLD
import it.unibo.tuprolog.solve.solver.statemachine.state.StateEnd
import it.unibo.tuprolog.solve.solver.statemachine.state.StateGoalEvaluation
import it.unibo.tuprolog.solve.solver.statemachine.state.StateRuleSelection
import it.unibo.tuprolog.solve.solver.testutils.SolverTestUtils

/**
 * Utils singleton to help testing [StateGoalEvaluation]
 *
 * @author Enrico
 */
internal object StateGoalEvaluationUtils {

    /** A side effect manager impl */
    internal val expectedSideEffectImpl = SideEffectManagerImpl()

    /** A context with [expectedSideEffectImpl] */
    private val contextWithExpectedSideEffectImpl = ExecutionContextImpl(sideEffectManager = expectedSideEffectImpl)

    /** Map containing requests that should make StateGoalEvaluation go into predicted state */
    internal val requestToNextStatesMap by lazy {
        mapOf(
                createPrimitiveRequest { sequenceOf(Solve.Response(Solution.Yes(it.signature, it.arguments, it.context.substitution), sideEffectManager = expectedSideEffectImpl)) }
                        to Pair(1, StateEnd.True::class),

                createPrimitiveRequest {
                    sequenceOf(
                            Solve.Response(Solution.Yes(it.signature, it.arguments, it.context.substitution), sideEffectManager = expectedSideEffectImpl),
                            Solve.Response(Solution.Yes(it.signature, it.arguments, it.context.substitution), sideEffectManager =  expectedSideEffectImpl)
                    )
                } to Pair(2, StateEnd.True::class),

                createPrimitiveRequest { sequenceOf(Solve.Response(Solution.No(it.signature, it.arguments), sideEffectManager = expectedSideEffectImpl)) }
                        to Pair(1, StateEnd.False::class),

                createPrimitiveRequest { sequenceOf(Solve.Response(Solution.Halt(it.signature, it.arguments, HaltException(context = it.context)), sideEffectManager = expectedSideEffectImpl)) }
                        to Pair(1, StateEnd.Halt::class),

                createPrimitiveRequest { throw HaltException(context = contextWithExpectedSideEffectImpl) }
                        to Pair(1, StateEnd.Halt::class),

                createPrimitiveRequest {
                    sequence {
                        yieldAll(SolverSLD().solve(createPrimitiveRequest { throw HaltException(context = contextWithExpectedSideEffectImpl) }))
                    }
                } to Pair(1, StateEnd.Halt::class),

                createPrimitiveRequest {
                    sequence {
                        yieldAll(SolverSLD().solve(createPrimitiveRequest { throw HaltException(context = contextWithExpectedSideEffectImpl) }))
                        yield(Solve.Response(Solution.No(it.signature, it.arguments), sideEffectManager = expectedSideEffectImpl))
                    }
                } to Pair(1, StateEnd.Halt::class),

                *StateInitUtils.notVarargPrimitiveAndWellFormedGoalRequests.map { it to Pair(1, StateRuleSelection::class) }.toTypedArray()
        )
    }

    /** Map containing primitive requests that throw [PrologError] instances */
    internal val exceptionThrowingPrimitiveRequests by lazy {
        mapOf(
                createPrimitiveRequest { throw InstantiationError(context = it.context) } to Pair(1, StateEnd.Halt::class),
                createPrimitiveRequest { throw SystemError(context = it.context) } to Pair(1, StateEnd.Halt::class),
                createPrimitiveRequest { throw TypeError(context = it.context, expectedType = TypeError.Expected.CALLABLE, actualValue = Var.of("X")) } to Pair(1, StateEnd.Halt::class)
        )
    }

    /** Creates a request launching exactly given primitive behaviour */
    private fun createPrimitiveRequest(primitiveBehaviour: Primitive) =
            object : PrimitiveWrapper<ExecutionContext>(Signature("testPrimitive", 0)) {
                override fun uncheckedImplementation(request: Solve.Request<*>): Sequence<Solve.Response> = primitiveBehaviour(request)
            }.run {
                SolverTestUtils.createSolveRequest(
                        signature withArgs emptyList(),
                        primitives = mapOf(descriptionPair, Throw.descriptionPair)
                )
            }
}