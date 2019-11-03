package it.unibo.tuprolog.solve.solver.fsm.state.impl

import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateGoalEvaluationUtils
import it.unibo.tuprolog.solve.solver.fsm.state.testutils.StateUtils.assertStateTypeAndContext
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [StateGoalEvaluation]
 *
 * @author Enrico
 */
internal class StateGoalEvaluationTest {

    @Test
    fun stateGoalEvaluationNextStateIsComputedCorrectly() {
        StateGoalEvaluationUtils.requestToNextStatesMap.forEach { (request, expectedStates) ->
            val nextStates = StateGoalEvaluation(request).behave().toList()

            val (expectedNumber, expectedType) = expectedStates
            assertEquals(expectedNumber, nextStates.count())

            nextStates.forEach {
                assertStateTypeAndContext(expectedType, StateGoalEvaluationUtils.expectedSideEffectImpl, it)
            }
        }
    }

    @Test
    fun stateGoalEvaluationNextStateOnPrologErrorIsSameStateWithThrowRequest() {
        StateGoalEvaluationUtils.exceptionThrowingPrimitiveRequests.forEach { (request, nextState) ->
            val nextStates = StateGoalEvaluation(request).behave().toList()

            val (expectedNumber, expectedType) = nextState
            assertEquals(expectedNumber, nextStates.count())

            nextStates.forEach { assertEquals(expectedType, it::class) }
        }
    }

}
