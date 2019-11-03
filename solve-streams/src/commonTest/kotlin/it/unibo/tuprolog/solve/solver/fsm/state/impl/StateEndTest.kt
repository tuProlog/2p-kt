package it.unibo.tuprolog.solve.solver.fsm.state.impl

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateEndUtils.aNoResponse
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateEndUtils.aYesResponse
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateEndUtils.allResponseTypes
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateEndUtils.anExceptionalResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

/**
 * Test class for [StateEnd] and subclasses
 *
 * @author Enrico
 */
internal class StateEndTest {

    private val stateEndCorrectInstances by lazy {
        listOf(
                StateEnd.True(aYesResponse),
                StateEnd.False(aNoResponse),
                StateEnd.Halt(anExceptionalResponse)
        )
    }

    @Test
    fun allStateEndInstancesBehaveDoesNothing() {
        stateEndCorrectInstances.forEach { assertEquals(emptySequence(), it.behave()) }
    }

    @Test
    fun trueContainsInsertedData() {
        assertSame(aYesResponse, StateEnd.True(aYesResponse).solve)
    }

    @Test
    fun trueConstructorComplainsIfNotCorrectSolutionInResponse() {
        allResponseTypes.filterNot { it.solution is Solution.Yes }.forEach {
            assertFailsWith<IllegalArgumentException> { StateEnd.True(it) }
        }
    }

    @Test
    fun falseContainsInsertedData() {
        assertSame(aNoResponse, StateEnd.False(aNoResponse).solve)
    }

    @Test
    fun falseConstructorComplainsIfNotCorrectSolutionInResponse() {
        allResponseTypes.filterNot { it.solution is Solution.No }.forEach {
            assertFailsWith<IllegalArgumentException> { StateEnd.False(it) }
        }
    }

    @Test
    fun haltContainsInsertedData() {
        assertSame(anExceptionalResponse, StateEnd.Halt(anExceptionalResponse).solve)
        assertSame((anExceptionalResponse.solution as Solution.Halt).exception, StateEnd.Halt(anExceptionalResponse).exception)
    }

    @Test
    fun haltConstructorComplainsIfNotCorrectSolutionInResponse() {
        allResponseTypes.filterNot { it.solution is Solution.Halt }.forEach {
            assertFailsWith<IllegalArgumentException> { StateEnd.Halt(it) }
        }
    }
}
