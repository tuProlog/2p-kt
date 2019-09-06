package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.solve.primitiveimpl.testutils.CallUtils
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [Call]
 *
 * @author Enrico
 */
internal class CallTest {

    @Test
    @Ignore
    fun ifCallArgumentIsVariableThrowsInstantiationError() {
        val toBeTested = Call.primitive(CallUtils.requestOfCallWithNotInstantiatedVar)

        // TODO: 06/09/2019
    }

    @Test
    @Ignore
    fun ifCallArgumentIsNotWellFormedThrowsTypeErrorCallableWithGoal() {
        val toBeTested = Call.primitive(CallUtils.requestOfCallWithMalformedGoal)

        // TODO: 06/09/2019
    }

    @Test
    fun ifArgumentWellFormedGoalNotVariableItForwardsResponsesFromArgumentExecution() {
        CallUtils.requestSolutionMap.forEach { (request, solution) ->
            val response = Call.primitive(request).toList()

            assertEquals(solution.count(), response.count())

            response.map { it.solution }.zip(solution).forEach { (expected, actual) -> assertEquals(expected, actual) }
        }
    }

    @Test
    fun callShouldLimitCutPowersToTheInnerGoal() {
        val (request, solutionList) = CallUtils.requestToSolutionOfCallWithCut
        val toBeTested = Call.primitive(request).toList()

        assertEquals(solutionList.count(), toBeTested.count())

        solutionList.zip(toBeTested.map { it.solution }).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }
}
