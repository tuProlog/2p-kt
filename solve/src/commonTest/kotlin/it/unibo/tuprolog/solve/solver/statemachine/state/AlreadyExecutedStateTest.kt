package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.solver.statemachine.testutils.StateMachineExecutorUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Test class for [AlreadyExecutedState]
 *
 * @author Enrico
 */
internal class AlreadyExecutedStateTest {

    private val endState = StateMachineExecutorUtils.endState

    @Test
    fun holdsPassedState() {
        assertSame(endState, AlreadyExecutedState(endState).wrappedState)
    }

    @Test
    fun itsBehaviourIsBackedByWrappedState() {
        assertEquals(endState.behave(), AlreadyExecutedState(endState).behave())
    }

    @Test
    fun hasBehavedIsAlwaysTrue() {
        assertTrue { AlreadyExecutedState(endState).hasBehaved }
    }

    @Test
    fun alreadyExecutedStateExtensionFunctionCreatesAnInstanceWrappingReceiverState() {
        val correct = AlreadyExecutedState(endState)
        val toBeTested = endState.alreadyExecuted()

        assertEquals(correct.wrappedState, toBeTested.wrappedState)
        assertEquals(correct.behave(), toBeTested.behave())
        assertEquals(correct.hasBehaved, toBeTested.hasBehaved)
    }

    @Test
    fun alreadyExecutedStateIsIdempotentOnAlreadyWrappedStates() {
        val correct = endState.alreadyExecuted()
        val toBeTested = endState.alreadyExecuted().alreadyExecuted()

        assertSame(correct.wrappedState, toBeTested.wrappedState)
    }

}
