package it.unibo.tuprolog.solve.solver.statemachine.state.testutils

import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.ExecutionContextImpl
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.prologerror.InstantiationError
import it.unibo.tuprolog.solve.exception.prologerror.SystemError
import it.unibo.tuprolog.solve.exception.prologerror.TypeError
import it.unibo.tuprolog.solve.primitiveimpl.PrimitiveWrapper
import it.unibo.tuprolog.solve.primitiveimpl.Throw
import it.unibo.tuprolog.solve.solver.SolverSLD
import it.unibo.tuprolog.solve.solver.statemachine.state.StateEnd
import it.unibo.tuprolog.solve.solver.statemachine.state.StateGoalEvaluation
import it.unibo.tuprolog.solve.solver.statemachine.state.StateRuleSelection
import it.unibo.tuprolog.solve.solver.testutils.SolverTestUtils
import it.unibo.tuprolog.solve.testutils.DummyInstances

/**
 * Utils singleton to help testing [StateGoalEvaluation]
 *
 * @author Enrico
 */
internal object StateGoalEvaluationUtils {

    /** A different context from [DummyInstances.executionContext] instance */
    internal val contextDifferentFromDummy = DummyInstances.executionContext.copy()

    /** Map containing requests that should make StateGoalEvaluation go into predicted state */
    internal val requestToNextStatesMap by lazy {
        mapOf(
                createPrimitiveRequest { sequenceOf(Solve.Response(Solution.Yes(it.signature, it.arguments, it.context.substitution), context = contextDifferentFromDummy)) }
                        to Pair(1, StateEnd.True::class),

                createPrimitiveRequest {
                    sequenceOf(
                            Solve.Response(Solution.Yes(it.signature, it.arguments, it.context.substitution), context = contextDifferentFromDummy),
                            Solve.Response(Solution.Yes(it.signature, it.arguments, it.context.substitution), context =  contextDifferentFromDummy)
                    )
                } to Pair(2, StateEnd.True::class),

                createPrimitiveRequest { sequenceOf(Solve.Response(Solution.No(it.signature, it.arguments), context = contextDifferentFromDummy)) }
                        to Pair(1, StateEnd.False::class),

                createPrimitiveRequest { sequenceOf(Solve.Response(Solution.Halt(it.signature, it.arguments, HaltException(context = it.context)), context = contextDifferentFromDummy)) }
                        to Pair(1, StateEnd.Halt::class),

                createPrimitiveRequest { throw HaltException(context = contextDifferentFromDummy) }
                        to Pair(1, StateEnd.Halt::class),

                createPrimitiveRequest {
                    sequence {
                        yieldAll(SolverSLD().solve(createPrimitiveRequest { throw HaltException(context = contextDifferentFromDummy) }))
                    }
                } to Pair(1, StateEnd.Halt::class),

                createPrimitiveRequest {
                    sequence {
                        yieldAll(SolverSLD().solve(createPrimitiveRequest { throw HaltException(context = contextDifferentFromDummy) }))
                        yield(Solve.Response(Solution.No(it.signature, it.arguments), context = contextDifferentFromDummy))
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
    private fun createPrimitiveRequest(primitiveBehaviour: (Solve.Request<ExecutionContextImpl>) -> Sequence<Solve.Response>) =
            object : PrimitiveWrapper(Signature("testPrimitive", 0)) {
                override val uncheckedImplementation: Primitive = primitiveBehaviour as Primitive
            }.run {
                SolverTestUtils.createSolveRequest(
                        signature withArgs emptyList(),
                        primitives = mapOf(descriptionPair, Throw.descriptionPair)
                )
            }
}