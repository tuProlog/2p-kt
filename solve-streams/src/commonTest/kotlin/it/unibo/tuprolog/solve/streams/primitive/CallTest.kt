package it.unibo.tuprolog.solve.streams.primitive

import it.unibo.tuprolog.solve.assertOverFailure
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.streams.primitive.testutils.CallUtils.assertErrorCauseChainComputedCorrectly
import it.unibo.tuprolog.solve.streams.primitive.testutils.CallUtils.requestSolutionMap
import it.unibo.tuprolog.solve.streams.primitive.testutils.CallUtils.requestToErrorSolutionMap
import it.unibo.tuprolog.solve.streams.primitive.testutils.CallUtils.requestToSolutionOfCallWithCut
import it.unibo.tuprolog.solve.streams.primitive.testutils.PrimitiveUtils.deepCause
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Call
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
        requestSolutionMap.forEach { (request, solutionList) ->
            val toBeTested = Call.implementation.solve(request).toList()

            assertSolutionEquals(solutionList, toBeTested.map { it.solution })
        }
    }

    @Test
    fun callThrowExceptionIfCallArgIsVariableOrNotWellFormed() {
        requestToErrorSolutionMap.forEach { (request, solutionList) ->
            assertFailsWith(solutionList.single().deepCause()::class) { Call.implementation.solve(request) }
        }
    }

    @Test
    fun callPrimitiveErrorContainsCorrectContext() {
        requestToErrorSolutionMap.forEach { (request, _) ->
            assertOverFailure<ResolutionException>({ Call.implementation.solve(request) }) {
                assertEquals(request.context, it.context)
            }
        }
    }

    @Test
    fun callShouldLimitCutPowersToTheInnerGoal() {
        requestToSolutionOfCallWithCut.forEach { (request, solutionList) ->
            val toBeTested = Call.implementation.solve(request).toList()

            assertSolutionEquals(solutionList, toBeTested.map { it.solution })
        }
    }

    @Test
    fun callErrorChainComputedCorrectly() {
        requestToErrorSolutionMap.forEach { (request, solutionList) ->
            assertErrorCauseChainComputedCorrectly(request, solutionList.single())
        }
    }
}
