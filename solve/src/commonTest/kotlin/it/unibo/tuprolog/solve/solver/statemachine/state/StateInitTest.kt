package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.solver.SolverUtils.prepareForExecution
import it.unibo.tuprolog.solve.solver.statemachine.state.testutils.StateGoalSelectionUtils
import it.unibo.tuprolog.solve.testutils.DummyInstances
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Test class for [StateInit]
 *
 * @author Enrico
 */
internal class StateInitTest {

    @Test
    fun ifCurrentGoalMatchesSolverSuccessCheckStrategy() {
        // precondition
        assertTrue {
            with(DummyInstances.solveRequest.context) {
                solverStrategies.successCheckStrategy(Signature("true", 0).withArgs(emptyList())!!, this)
            }
        }

        val nextStates = StateInit(StateGoalSelectionUtils.trueSolveRequest, DummyInstances.executionStrategy).behave()

        assertEquals(1, nextStates.count())
        assertTrue { nextStates.single() is StateEnd.True }
    }

    @Test
    fun ifCurrentGoalIsAVarargPrimitive() {
        val nextStates = StateInit(StateGoalSelectionUtils.varargPrimitive, DummyInstances.executionStrategy).behave()

        assertEquals(1, nextStates.count())
        assertTrue { nextStates.single() is StateGoalEvaluation }
    }

    @Test
    fun ifCurrentGoalIsAPrimitiveOrAWellFormedGoal() {
        StateGoalSelectionUtils.notVarargPrimitiveAndWellFormedGoalRequests
                .filterNot { it == StateGoalSelectionUtils.preparationNeededGoal }
                .forEach {
                    val nextStates = StateInit(it, DummyInstances.executionStrategy).behave()

                    assertEquals(1, nextStates.count())
                    assertTrue { nextStates.single() is StateGoalEvaluation }

                    assertEquals(it.query, nextStates.first().solveRequest.query)
                }
    }

    @Test
    fun ifCurrentGoalNeedPreparationForExecution() {
        val nextStates = StateInit(StateGoalSelectionUtils.preparationNeededGoal, DummyInstances.executionStrategy).behave()

        assertEquals(
                prepareForExecution(StateGoalSelectionUtils.preparationNeededGoal.query!!),
                nextStates.first().solveRequest.query
        )
    }

    @Test
    fun ifGoalNotWellFormed() {
        val nextStates = StateInit(StateGoalSelectionUtils.nonWellFormedGoal, DummyInstances.executionStrategy).behave()

        assertEquals(1, nextStates.count())
        assertTrue { nextStates.single() is StateEnd.False }
    }

    @Test
    fun stateInitializationCreatesNewContextAddingAsParentTheCurrentOne() {
        val toBeTested = StateInit(DummyInstances.solveRequest, DummyInstances.executionStrategy).behave().single()

        assertSame(toBeTested.solveRequest.context.clauseScopedParents.single(), DummyInstances.executionContext)
    }

    @Test
    fun stateInitializationResetsToFalseIsChoicePointChild() {
        val myState = StateInit(
                with(DummyInstances.solveRequest) { copy(context = context.copy(isChoicePointChild = true)) },
                DummyInstances.executionStrategy
        )
        val toBeTested = myState.behave().single()
        assertEquals(false, toBeTested.solveRequest.context.isChoicePointChild)
        assertEquals(true, toBeTested.solveRequest.context.clauseScopedParents.single().isChoicePointChild)
    }
}
