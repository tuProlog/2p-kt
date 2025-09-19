package it.unibo.tuprolog.solve.streams.solver.fsm

import it.unibo.tuprolog.solve.streams.solver.fsm.StateMachineExecutor.unwrapIfNeeded
import it.unibo.tuprolog.solve.streams.solver.fsm.testutils.StateMachineExecutorUtils.allFiniteStateMachines
import it.unibo.tuprolog.solve.streams.solver.fsm.testutils.StateMachineExecutorUtils.allNextStatesFromThis
import it.unibo.tuprolog.solve.streams.solver.fsm.testutils.StateMachineExecutorUtils.eightLeafSearchTreeState
import it.unibo.tuprolog.solve.streams.solver.fsm.testutils.StateMachineExecutorUtils.endState
import it.unibo.tuprolog.solve.streams.solver.fsm.testutils.StateMachineExecutorUtils.oneShotState
import it.unibo.tuprolog.solve.streams.solver.fsm.testutils.StateMachineExecutorUtils.oneStateAtATimeState
import it.unibo.tuprolog.solve.streams.solver.fsm.testutils.StateMachineExecutorUtils.thirdStateHasAlreadyBehaved
import it.unibo.tuprolog.solve.streams.solver.fsm.testutils.StateMachineExecutorUtils.threeShotState
import it.unibo.tuprolog.solve.streams.solver.fsm.testutils.StateMachineExecutorUtils.threeShotStateAlreadyExecuted
import it.unibo.tuprolog.solve.streams.solver.fsm.testutils.StateMachineExecutorUtils.toBehavedState
import it.unibo.tuprolog.solve.streams.solver.fsm.testutils.StateMachineExecutorUtils.twoAlternativeState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.test.fail

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

    @Test
    fun executeOfAlreadyBehavedStatesDoesNotExecuteTheBehaviour() {
        val behavedStates = allFiniteStateMachines.map { toBehavedState(it) } + threeShotStateAlreadyExecuted
        val stateSequences = behavedStates.map { StateMachineExecutor.execute(it) }

        stateSequences.forEach { assertEquals(emptySequence(), it) }
    }

    @Test
    fun executeRespectsHasBehavedEvenDeeperInComputation() {
        // those first tests are a precondition for verifying the subsequent property
        val normalSituation = oneStateAtATimeState(2, oneStateAtATimeState(2, endState))
        val stateSequence = StateMachineExecutor.execute(normalSituation)
        assertEquals(6, stateSequence.count())
        assertEquals(endState, stateSequence.toList().getOrNull(5))

        val toBeTested = StateMachineExecutor.execute(thirdStateHasAlreadyBehaved)

        assertEquals(3, toBeTested.count())
        toBeTested
            .toList()
            .getOrNull(2)
            ?.run { assertTrue(hasBehaved) }
            ?: fail("No state at that index")
    }

    @Test
    fun executeUnwrapsAlreadyExecutedStatesIfNeeded() {
        val nextStates = 3
        val stateSequence =
            StateMachineExecutor.execute(allNextStatesFromThis(nextStates, AlreadyExecutedState(endState)))
        assertEquals(nextStates, stateSequence.count())
        stateSequence.forEach { assertSame(endState, it) }
    }

    @Test
    fun executeWrappingWorksAsExecuteButWrappingAllStatesIntoAlreadyExecutedState() {
        val correctlyExecuted = allFiniteStateMachines.map { StateMachineExecutor.execute(it).toList() }
        val toBeTested = allFiniteStateMachines.map { StateMachineExecutor.executeWrapping(it).toList() }

        assertEquals(correctlyExecuted.flatten().count(), toBeTested.flatten().count())
        assertEquals(
            correctlyExecuted.flatten().map { it.toString() },
            toBeTested.flatten().map { it.wrappedState.toString() },
        )
    }

    @Test
    fun executeWrappingCanBeUsedTransparentlyInternallyBeforeExternalExecuteCall() {
        val mixedBehavedAndNonBehavedStates =
            object : State by endState {
                override val hasBehaved: Boolean = false

                override fun behave(): Sequence<State> = StateMachineExecutor.executeWrapping(threeShotState)
            }

        val stateSequence = StateMachineExecutor.execute(mixedBehavedAndNonBehavedStates)
        assertEquals(4, stateSequence.count())
        stateSequence.forEach { assertFalse { it.hasBehaved } }
    }

    @Test
    fun unwrapIfNeededWorksAsExpected() {
        allFiniteStateMachines.forEach {
            assertSame(it, AlreadyExecutedState(it).unwrapIfNeeded())
            assertSame(it, it.unwrapIfNeeded())
        }
    }
}
