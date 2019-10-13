package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.libraries.stdlib.testutils.CallUtils
import it.unibo.tuprolog.libraries.stdlib.testutils.PrimitivesUtils.assertErrorCauseChainComputedCorrectly
import it.unibo.tuprolog.libraries.stdlib.testutils.PrimitivesUtils.assertRequestContextEqualToThrownErrorOne
import it.unibo.tuprolog.solve.solver.testutils.SolverSLDUtils.assertSolutionsCorrect
import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * Test class for [Call]
 *
 * @author Enrico
 */
internal class CallTest {

    @Test
    fun callForwardsResponsesFromArgumentExecutionIfWellFormedGoalAndNotVariable() {
        CallUtils.requestSolutionMap.forEach { (request, solutionList) ->
            val toBeTested = Call.primitive(request).toList()

            assertSolutionsCorrect(solutionList, toBeTested.map { it.solution })
        }
    }

    @Test
    fun callThrowExceptionIfCallArgIsVariableOrNotWellFormed() {
        CallUtils.exposedErrorThrowingRequests.forEach { (request, errorType) ->
            assertFailsWith(errorType) { Call.primitive(request) }
        }
    }

    @Test
    fun callPrimitiveErrorContainsCorrectContext() {
        CallUtils.exposedErrorThrowingRequests.forEach { (request, _) ->
            assertRequestContextEqualToThrownErrorOne(request, Call)
        }
    }

    @Test
    fun callShouldLimitCutPowersToTheInnerGoal() {
        val (request, solutionList) = CallUtils.requestToSolutionOfCallWithCut
        val toBeTested = Call.primitive(request).toList()

        assertSolutionsCorrect(solutionList, toBeTested.map { it.solution })
    }

    @Test
    fun callErrorChainComputedCorrectly() {
        CallUtils.requestToErrorSolutionMap.forEach { (request, solutionList) ->
            assertErrorCauseChainComputedCorrectly(request, solutionList.single())
        }
    }
}
