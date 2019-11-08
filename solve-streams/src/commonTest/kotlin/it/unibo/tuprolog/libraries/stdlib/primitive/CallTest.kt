package it.unibo.tuprolog.libraries.stdlib.primitive

import it.unibo.tuprolog.libraries.stdlib.primitive.testutils.CallUtils
import it.unibo.tuprolog.libraries.stdlib.primitive.testutils.PrimitivesUtils.assertErrorCauseChainComputedCorrectly
import it.unibo.tuprolog.solve.assertOverFailure
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import kotlin.test.Test
import kotlin.test.assertEquals
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
            val toBeTested = Call.wrappedImplementation(request).toList()

            assertSolutionEquals(solutionList, toBeTested.map { it.solution })
        }
    }

    @Test
    fun callThrowExceptionIfCallArgIsVariableOrNotWellFormed() {
        CallUtils.exposedErrorThrowingRequests.forEach { (request, errorType) ->
            assertFailsWith(errorType) { Call.wrappedImplementation(request) }
        }
    }

    @Test
    fun callPrimitiveErrorContainsCorrectContext() {
        CallUtils.exposedErrorThrowingRequests.forEach { (request, _) ->
            assertOverFailure<TuPrologRuntimeException>({ Call.wrappedImplementation(request) }) {
                assertEquals(request.context, it.context)
            }
        }
    }

    @Test
    fun callShouldLimitCutPowersToTheInnerGoal() {
        val (request, solutionList) = CallUtils.requestToSolutionOfCallWithCut
        val toBeTested = Call.wrappedImplementation(request).toList()

        assertSolutionEquals(solutionList, toBeTested.map { it.solution })
    }

    @Test
    fun callErrorChainComputedCorrectly() {
        CallUtils.requestToErrorSolutionMap.forEach { (request, solutionList) ->
            assertErrorCauseChainComputedCorrectly(request, solutionList.single())
        }
    }
}
