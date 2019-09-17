package it.unibo.tuprolog.solve.solver.statemachine.state.testutils

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.primitiveimpl.PrimitiveWrapper
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
                createPrimitiveRequest(primitiveThatAlwaysSucceeds) to Pair(1, StateEnd.True::class),
                createPrimitiveRequest(primitiveThatAlwaysSucceedsTwoTimes) to Pair(2, StateEnd.True::class),
                createPrimitiveRequest(primitiveThatAlwaysFails) to Pair(1, StateEnd.False::class),
                createPrimitiveRequest(primitiveThatAlwaysReturnsSolutionHalt) to Pair(1, StateEnd.Halt::class),
                createPrimitiveRequest(primitiveThrowingHaltException) to Pair(1, StateEnd.Halt::class),
                createPrimitiveRequest(primitiveDeepThrowingHaltException) to Pair(1, StateEnd.Halt::class),
                createPrimitiveRequest(primitiveThatHaltsAndThenCouldHaveOtherSolutions) to Pair(1, StateEnd.Halt::class),
                *StateGoalSelectionUtils.notVarargPrimitiveAndWellFormedGoalRequests.map { it to Pair(1, StateRuleSelection::class) }.toTypedArray()
        )
    }

    /** A primitive that always succeeds, returning a different context instance as response */
    private val primitiveThatAlwaysSucceeds = object : PrimitiveWrapper(Signature("ciao", 0)) {
        override val uncheckedImplementation: Primitive = {
            sequenceOf(Solve.Response(
                    Solution.Yes(it.signature, it.arguments, it.context.currentSubstitution),
                    contextDifferentFromDummy
            ))
        }
    }

    /** A primitive that always succeeds two times, returning a different context instance as response */
    private val primitiveThatAlwaysSucceedsTwoTimes = object : PrimitiveWrapper(Signature("ciao2", 0)) {
        override val uncheckedImplementation: Primitive = {
            sequenceOf(
                    Solve.Response(
                            Solution.Yes(it.signature, it.arguments, it.context.currentSubstitution),
                            contextDifferentFromDummy
                    ),
                    Solve.Response(
                            Solution.Yes(it.signature, it.arguments, it.context.currentSubstitution),
                            contextDifferentFromDummy
                    )
            )
        }
    }

    /** A primitive that always succeeds, returning a different context instance as response */
    private val primitiveThatAlwaysFails = object : PrimitiveWrapper(Signature("ciaoFailed", 0)) {
        override val uncheckedImplementation: Primitive = {
            sequenceOf(Solve.Response(
                    Solution.No(it.signature, it.arguments),
                    contextDifferentFromDummy
            ))
        }
    }

    /** A primitive that always halts, returning a different context instance as response */
    private val primitiveThatAlwaysReturnsSolutionHalt = object : PrimitiveWrapper(Signature("ciaoHalted", 0)) {
        override val uncheckedImplementation: Primitive = {
            sequenceOf(Solve.Response(
                    Solution.Halt(it.signature, it.arguments, HaltException(context = it.context)),
                    contextDifferentFromDummy
            ))
        }
    }

    /** A primitive that always throws halt exception */
    private val primitiveThrowingHaltException = object : PrimitiveWrapper(Signature("haltException", 0)) {
        override val uncheckedImplementation: Primitive = {
            throw HaltException(context = contextDifferentFromDummy)
        }
    }

    /** A primitive calling another primitive that always throws halt exception */
    private val primitiveDeepThrowingHaltException = object : PrimitiveWrapper(Signature("deepHaltException", 0)) {
        override val uncheckedImplementation: Primitive = {
            sequence {
                yieldAll(SolverSLD().solve(createPrimitiveRequest(primitiveThrowingHaltException)))
            }
        }
    }

    /** A primitive returning a solution halt and other possibilities */
    private val primitiveThatHaltsAndThenCouldHaveOtherSolutions = object : PrimitiveWrapper(Signature("deepHaltAndOtherSolution", 0)) {
        override val uncheckedImplementation: Primitive = {
            sequence {
                yieldAll(SolverSLD().solve(createPrimitiveRequest(primitiveThrowingHaltException)))
                yield(Solve.Response(
                        Solution.No(it.signature, it.arguments),
                        contextDifferentFromDummy
                ))
            }
        }
    }

    /** Creates a request launching exactly given primitive */
    private fun createPrimitiveRequest(primitiveWrapper: PrimitiveWrapper, arguments: List<Term> = emptyList()) =
            SolverTestUtils.createSolveRequest(
                    primitiveWrapper.signature.withArgs(arguments)!!,
                    primitives = mapOf(primitiveWrapper.descriptionPair)
            )
}