package it.unibo.tuprolog.solve.solver.statemachine.state

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

    private val state = StateInit(DummyInstances.solveRequest, DummyInstances.executionStrategy)

    @Test
    fun stateInitBehaveGoesAlwaysIntoGoalSelectionState() {
        val toBeTested = state.behave()

        assertEquals(1, toBeTested.count())
        assertTrue { toBeTested.single() is StateGoalSelection }
    }

    @Test
    fun stateInitInitializationCreatesNewContextAddingAsParentTheCurrentOne() {
        val toBeTested = state.behave().single()

        assertSame(toBeTested.solveRequest.context.parents.single(), DummyInstances.executionContext)
    }

    @Test
    fun stateInitInitializationResetsToFalseIsChoicePointChildForNextStates() {
        val myState = StateInit(
                with(DummyInstances.solveRequest) { copy(context = context.copy(isChoicePointChild = true)) },
                DummyInstances.executionStrategy
        )
        val toBeTested = myState.behave().single()
        assertEquals(false, toBeTested.solveRequest.context.isChoicePointChild)
        assertEquals(true, toBeTested.solveRequest.context.parents.single().isChoicePointChild)
    }

}
