package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.solver.statemachine.state.testutils.StateGoalEvaluationUtils
import it.unibo.tuprolog.solve.solver.statemachine.state.testutils.StateGoalEvaluationUtils.requestWithFailPrimitive
import it.unibo.tuprolog.solve.solver.statemachine.state.testutils.StateGoalEvaluationUtils.requestWithHaltingPrimitive
import it.unibo.tuprolog.solve.solver.statemachine.state.testutils.StateGoalEvaluationUtils.requestWithSuccessPrimitive
import it.unibo.tuprolog.solve.testutils.DummyInstances
import kotlin.test.*

/**
 * Test class for [StateGoalEvaluation]
 *
 * @author Enrico
 */
internal class StateGoalEvaluationTest {

    @Test
    fun ifSignatureNotPrimitiveOrPrimitiveNotFoundItGoesIntoStateRuleSelection() {
        StateGoalEvaluationUtils.evaluationToRuleSelectionRequests.forEach {
            val nextStates = StateGoalEvaluation(it, DummyInstances.executionStrategy).behave()

            assertEquals(1, nextStates.count())
            assertTrue { nextStates.single() is StateRuleSelection }
        }
    }

    @Test
    fun ifPrimitiveIsPresentInLibrariesItIsExecuted() {
        listOf(requestWithSuccessPrimitive, requestWithFailPrimitive, requestWithHaltingPrimitive).forEach {
            val nextStates = StateGoalEvaluation(it, DummyInstances.executionStrategy).behave()

            assertEquals(1, nextStates.count())
        }
    }

    @Test
    fun ifPrimitiveThrowsHaltExceptionItGoesIntoHaltState() {
        val nextStates = StateGoalEvaluation(requestWithHaltingPrimitive, DummyInstances.executionStrategy).behave()

        assertTrue { nextStates.single() is StateEnd.Halt }
        assertEquals(requestWithHaltingPrimitive.context, nextStates.single().solveRequest.context)
    }

    @Test
    fun ifPrimitiveReturnsSolutionYesItGoesIntoStateEndTrueCopyingPrimitiveGeneratedContext() {
        val nextStates = StateGoalEvaluation(requestWithSuccessPrimitive, DummyInstances.executionStrategy).behave()

        assertTrue { nextStates.single() is StateEnd.True }
        assertNotSame(requestWithSuccessPrimitive.context, nextStates.single().solveRequest.context)
        assertSame(StateGoalEvaluationUtils.contextDifferentFromDummy, nextStates.single().solveRequest.context)
    }

    @Test
    fun ifPrimitiveReturnsSolutionNoItGoesIntoStateEndFalseCopyingPrimitiveGeneratedContext() {
        val nextStates = StateGoalEvaluation(requestWithFailPrimitive, DummyInstances.executionStrategy).behave()

        assertTrue { nextStates.single() is StateEnd.False }
        assertNotSame(requestWithFailPrimitive.context, nextStates.single().solveRequest.context)
        assertSame(StateGoalEvaluationUtils.contextDifferentFromDummy, nextStates.single().solveRequest.context)
    }

}
