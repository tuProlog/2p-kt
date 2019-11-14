package it.unibo.tuprolog.solve.solver.fsm.impl

import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateGoalEvaluationUtils
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateGoalEvaluationUtils.createRequestForPrimitiveResponding
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateGoalEvaluationUtils.expectedContext
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateGoalEvaluationUtils.primitiveRequestThrowingPrologError
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateUtils.assertOnlyOneNextState
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateUtils.getSideEffectsManager
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [StateGoalEvaluation]
 *
 * @author Enrico
 */
internal class StateGoalEvaluationTest {

    @Test
    fun signaturesNotRecognizedAsPrimitivesFlowToStateRuleSelection() {
        StateGoalEvaluationUtils.nonPrimitiveRequests.forEach { request ->
            val nextStates = StateGoalEvaluation(request).behave()

            assertOnlyOneNextState<StateRuleSelection>(nextStates)
        }
    }

    @Test
    fun signaturesRecognizedAsPrimitivesAreExecuted() {
        StateGoalEvaluationUtils.primitiveRequestToNextStateList.forEach { (request, expectedStates) ->
            val nextStates = StateGoalEvaluation(request).behave()

            assertEquals(expectedStates, nextStates.map { it::class }.toList())
        }
    }

    @Test
    fun returningSolutionHaltHasSameBehaviourOfThrowingTheException() {
        val throwingPrimitive = createRequestForPrimitiveResponding { throw HaltException(context = expectedContext) }
        val nonThrowingPrimitive = createRequestForPrimitiveResponding {
            sequenceOf(
                    it.replyException(HaltException(context = expectedContext)),
                    it.replyFail()
            )
        }

        val throwingEndState = StateGoalEvaluation(throwingPrimitive).behave().single()
        val nonThrowingEndState = StateGoalEvaluation(nonThrowingPrimitive).behave().single()

        assertEquals(
                throwingEndState.solve.getSideEffectsManager(),
                nonThrowingEndState.solve.getSideEffectsManager()
        )
    }

    @Test
    fun stateGoalEvaluationNextStateOnPrologErrorIsSameStateWithThrowRequest() {
        primitiveRequestThrowingPrologError.forEach { request ->
            val nextStates = StateGoalEvaluation(request).behave()

            assertOnlyOneNextState<StateEnd.Halt>(nextStates)
        }
    }

}
