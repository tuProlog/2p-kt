package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.solve.primitiveimpl.testutils.CallUtils
import it.unibo.tuprolog.solve.solver.testutils.SolverSLDUtils.assertSolutionsCorrect
import kotlin.test.Ignore
import kotlin.test.Test

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
        CallUtils.requestSolutionMap.forEach { (request, solutionList) ->
            val toBeTested = Call.primitive(request).toList()

            assertSolutionsCorrect(solutionList, toBeTested.map { it.solution })
        }
    }

    @Test
    fun callShouldLimitCutPowersToTheInnerGoal() {
        val (request, solutionList) = CallUtils.requestToSolutionOfCallWithCut
        val toBeTested = Call.primitive(request).toList()

        assertSolutionsCorrect(solutionList, toBeTested.map { it.solution })
    }
}
