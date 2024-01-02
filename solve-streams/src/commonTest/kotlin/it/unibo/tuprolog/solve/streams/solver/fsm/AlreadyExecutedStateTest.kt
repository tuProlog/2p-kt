package it.unibo.tuprolog.solve.streams.solver.fsm

import it.unibo.tuprolog.solve.streams.solver.fsm.testutils.StateMachineExecutorUtils
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
        val toBeTested = endState.asAlreadyExecuted()

        assertEquals(correct.wrappedState, toBeTested.wrappedState)
        assertEquals(correct.behave(), toBeTested.behave())
        assertEquals(correct.hasBehaved, toBeTested.hasBehaved)
    }

    @Test
    fun alreadyExecutedStateIsIdempotentOnAlreadyWrappedStates() {
        val correct = endState.asAlreadyExecuted()
        val toBeTested = endState.asAlreadyExecuted().asAlreadyExecuted()

        assertSame(correct.wrappedState, toBeTested.wrappedState)
    }
}
