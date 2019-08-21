package it.unibo.tuprolog.solve.solver.statemachine

import it.unibo.tuprolog.solve.solver.statemachine.testutils.StateMachineExecutorUtils.eightLeafSearchTreeState
import it.unibo.tuprolog.solve.solver.statemachine.testutils.StateMachineExecutorUtils.endState
import it.unibo.tuprolog.solve.solver.statemachine.testutils.StateMachineExecutorUtils.oneShotState
import it.unibo.tuprolog.solve.solver.statemachine.testutils.StateMachineExecutorUtils.threeShotState
import it.unibo.tuprolog.solve.solver.statemachine.testutils.StateMachineExecutorUtils.twoAlternativeState
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [StateMachineExecutor]
 *
 * @author Enrico
 */
internal class StateMachineExecutorTest {

    @Test
    fun executeOfOneShotStateWorksAsExpected() {
        val stateSequence = StateMachineExecutor.execute(oneShotState)
        assertEquals(1, stateSequence.count())
        assertEquals(endState, stateSequence.single())
    }

    @Test
    fun executeOfThreeShotStateWorksAsExpected() {
        val stateSequence = StateMachineExecutor.execute(threeShotState)
        assertEquals(4, stateSequence.count())
        assertEquals(endState, stateSequence.last())
    }

    @Test
    fun executeOfTwoAlternativeState() {
        val stateSequence = StateMachineExecutor.execute(twoAlternativeState)
        assertEquals(2, stateSequence.count())
        stateSequence.forEach { assertEquals(endState, it) }
    }

    @Test
    fun executeOfEightLeafSearchTreeState() {
        val stateSequence = StateMachineExecutor.execute(eightLeafSearchTreeState)
        assertEquals(14, stateSequence.count())
        assertEquals(8, stateSequence.filter { it == endState }.count())
    }

    @Test
    fun eightLeafSearchTreeVisitedDepthFirst() {
        val stateSequence = StateMachineExecutor.execute(eightLeafSearchTreeState)
        val endStateIndexes = stateSequence.withIndex().filter { it.value == endState }.map { it.index }

        assertEquals(listOf(2, 3, 5, 6, 9, 10, 12, 13), endStateIndexes.toList())
    }

}
