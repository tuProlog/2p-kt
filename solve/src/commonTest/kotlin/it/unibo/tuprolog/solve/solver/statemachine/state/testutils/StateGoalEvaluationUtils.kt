package it.unibo.tuprolog.solve.solver.statemachine.state.testutils

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.primitiveimpl.PrimitiveWrapper
import it.unibo.tuprolog.solve.solver.exception.HaltException
import it.unibo.tuprolog.solve.solver.statemachine.state.StateGoalEvaluation
import it.unibo.tuprolog.solve.solver.statemachine.state.StateRuleSelection
import it.unibo.tuprolog.solve.solver.testutils.SolverTestUtils
import it.unibo.tuprolog.solve.testutils.DummyInstances

/**
 * Utils singleto to help testing [StateGoalEvaluation]
 *
 * @author Enrico
 */
internal object StateGoalEvaluationUtils {

    /** Collection of Solve.Request that should pass [StateGoalEvaluation] going into [StateRuleSelection] */
    internal val evaluationToRuleSelectionRequests = StateGoalSelectionUtils.notVarargPrimitiveAndWellFormedGoalRequests

    /** A different context from [DummyInstances.executionContext] instance */
    internal val contextDifferentFromDummy = DummyInstances.executionContext.copy()

    /** A primitive that always succeeds, returning a different context instance as response */
    internal val primitiveThatAlwaysSucceeds = object : PrimitiveWrapper(Signature("ciao", 0)) {
        override val uncheckedImplementation: Primitive = {
            sequenceOf(Solve.Response(
                    Solution.Yes(it.signature, it.arguments, it.context.currentSubstitution),
                    contextDifferentFromDummy
            ))
        }
    }

    /** A primitive that always succeeds, returning a different context instance as response */
    internal val primitiveThatAlwaysFails = object : PrimitiveWrapper(Signature("ciaoFailed", 0)) {
        override val uncheckedImplementation: Primitive = {
            sequenceOf(Solve.Response(
                    Solution.No(it.signature, it.arguments),
                    contextDifferentFromDummy
            ))
        }
    }

    /** A primitive that always throws halt exception */
    internal val primitiveThrowingHaltException = object : PrimitiveWrapper(Signature("haltException", 0)) {
        override val uncheckedImplementation: Primitive = {
            throw HaltException()
        }
    }

    /** A request launching [primitiveThatAlwaysSucceeds] */
    internal val requestWithSuccessPrimitive = createRequest(primitiveThatAlwaysSucceeds)

    /** A request launching [primitiveThatAlwaysFails] */
    internal val requestWithFailPrimitive = createRequest(primitiveThatAlwaysFails)

    /** A request launching [requestWithHaltingPrimitive] */
    internal val requestWithHaltingPrimitive = createRequest(primitiveThrowingHaltException)

    /** Creates a request launching exactly given primitive */
    private fun createRequest(primitiveWrapper: PrimitiveWrapper, arguments: List<Term> = emptyList()) =
            SolverTestUtils.createSolveRequest(primitiveWrapper.signature.withArgs(arguments)!!,
                    primitives = mapOf(primitiveWrapper.descriptionPair))
}