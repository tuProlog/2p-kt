package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.solver.statemachine.state.testutils.StateGoalEvaluationUtils
import it.unibo.tuprolog.solve.solver.statemachine.state.testutils.StateUtils.assertStateTypeAndContext
import it.unibo.tuprolog.solve.testutils.DummyInstances
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
            val nextStates = StateGoalEvaluation(request, DummyInstances.executionStrategy).behave().toList()

            val (expectedNumber, expectedType) = expectedStates
            assertEquals(expectedNumber, nextStates.count())

            nextStates.forEach {
                assertStateTypeAndContext(expectedType, StateGoalEvaluationUtils.contextDifferentFromDummy, it)
            }
        }
    }

}
