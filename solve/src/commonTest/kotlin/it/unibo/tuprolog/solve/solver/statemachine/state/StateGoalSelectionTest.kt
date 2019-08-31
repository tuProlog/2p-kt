package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.solver.SolverUtils.prepareForExecution
import it.unibo.tuprolog.solve.solver.statemachine.state.StateUtils.composeSignatureAndArgs
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

        val nextStates = StateGoalSelection(StateUtils.trueSolveRequest, DummyInstances.executionStrategy).behave()

        assertEquals(1, nextStates.count())
        assertTrue { nextStates.single() is StateEnd.True }
    }

    @Test
    fun ifCurrentGoalIsAVarargPrimitive() {
        val nextStates = StateGoalSelection(StateUtils.varargPrimitive, DummyInstances.executionStrategy).behave()

        assertEquals(1, nextStates.count())
        assertTrue { nextStates.single() is StateGoalEvaluation }
    }

    @Test
    fun ifCurrentGoalIsAPrimitiveOrAWellFormedGoal() {
        StateUtils.notVarargPrimitiveAndWellFormedGoalRequests
                .filterNot { it == StateUtils.preparationNeededGoal }
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
        val nextStates = StateGoalSelection(StateUtils.preparationNeededGoal, DummyInstances.executionStrategy).behave()

        assertEquals(
                prepareForExecution(composeSignatureAndArgs(StateUtils.preparationNeededGoal)),
                composeSignatureAndArgs(nextStates.first().solveRequest)
        )
    }

    @Test
    fun ifGoalNotWellFormed() {
        val nextStates = StateGoalSelection(StateUtils.nonWellFormedGoal, DummyInstances.executionStrategy).behave()

        assertEquals(1, nextStates.count())
        assertTrue { nextStates.single() is StateEnd.False }
    }

    @Test
    fun shiftingToNextStateDoesntModifyContext() {
        StateUtils.notVarargPrimitiveAndWellFormedGoalRequests.forEach {
            val nextStates = StateGoalSelection(it, DummyInstances.executionStrategy).behave()

            assertEquals(DummyInstances.executionContext, nextStates.single().solveRequest.context)
            assertEquals(it.context.parents.toList(), nextStates.single().solveRequest.context.parents.toList())
        }
    }

}
