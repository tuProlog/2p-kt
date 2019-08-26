package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.testutils.DummyInstances
import kotlin.test.Test
import kotlin.test.assertEquals
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

}
