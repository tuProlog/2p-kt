package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.solver.SolverUtils.prepareForExecution
import it.unibo.tuprolog.solve.solver.statemachine.state.testutils.StateGoalSelectionUtils
import it.unibo.tuprolog.solve.solver.statemachine.state.testutils.StateGoalSelectionUtils.composeSignatureAndArgs
import it.unibo.tuprolog.solve.testutils.DummyInstances
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [StateGoalSelection]
 *
 * @author Enrico
 */
internal class StateGoalSelectionTest {

    @Test
    fun ifCurrentGoalMatchesSolverSuccessCheckStrategy() {
        // precondition
        assertTrue {
            with(DummyInstances.solveRequest.context) {
                solverStrategies.successCheckStrategy(Signature("true", 0).withArgs(emptyList())!!, this)
            }
        }

        val nextStates = StateGoalSelection(StateGoalSelectionUtils.trueSolveRequest, DummyInstances.executionStrategy).behave()

        assertEquals(1, nextStates.count())
        assertTrue { nextStates.single() is StateEnd.True }
    }

    @Test
    fun ifCurrentGoalIsAVarargPrimitive() {
        val nextStates = StateGoalSelection(StateGoalSelectionUtils.varargPrimitive, DummyInstances.executionStrategy).behave()

        assertEquals(1, nextStates.count())
        assertTrue { nextStates.single() is StateGoalEvaluation }
    }

    @Test
    fun ifCurrentGoalIsAPrimitiveOrAWellFormedGoal() {
        StateGoalSelectionUtils.notVarargPrimitiveAndWellFormedGoalRequests
                .filterNot { it == StateGoalSelectionUtils.preparationNeededGoal }
                .forEach {
                    val nextStates = StateGoalSelection(it, DummyInstances.executionStrategy).behave()

                    assertEquals(1, nextStates.count())
                    assertTrue { nextStates.single() is StateGoalEvaluation }

                    assertEquals(
                            composeSignatureAndArgs(it),
                            composeSignatureAndArgs(nextStates.first().solveRequest)
                    )
                }
    }

    @Test
    fun ifCurrentGoalNeedPreparationForExecution() {
        val nextStates = StateGoalSelection(StateGoalSelectionUtils.preparationNeededGoal, DummyInstances.executionStrategy).behave()

        assertEquals(
                prepareForExecution(composeSignatureAndArgs(StateGoalSelectionUtils.preparationNeededGoal)),
                composeSignatureAndArgs(nextStates.first().solveRequest)
        )
    }

    @Test
    fun ifGoalNotWellFormed() {
        val nextStates = StateGoalSelection(StateGoalSelectionUtils.nonWellFormedGoal, DummyInstances.executionStrategy).behave()

        assertEquals(1, nextStates.count())
        assertTrue { nextStates.single() is StateEnd.False }
    }

    @Test
    fun shiftingToNextStateDoesntModifyContext() {
        StateGoalSelectionUtils.notVarargPrimitiveAndWellFormedGoalRequests.forEach {
            val nextStates = StateGoalSelection(it, DummyInstances.executionStrategy).behave()

            assertEquals(DummyInstances.executionContext, nextStates.single().solveRequest.context)
            assertEquals(it.context.parents.toList(), nextStates.single().solveRequest.context.parents.toList())
        }
    }

}
