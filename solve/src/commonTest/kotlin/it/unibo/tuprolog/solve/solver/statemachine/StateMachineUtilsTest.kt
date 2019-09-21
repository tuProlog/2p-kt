package it.unibo.tuprolog.solve.solver.statemachine

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.solver.statemachine.StateMachineUtils.toStateEnd
import it.unibo.tuprolog.solve.solver.statemachine.state.StateEnd
import it.unibo.tuprolog.solve.testutils.DummyInstances
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [StateMachineUtils]
 *
 * @author Enrico
 */
internal class StateMachineUtilsTest {

    @Test
    fun toEndStateCreatesCorrectInstance() {
        val myException = HaltException(context = DummyInstances.executionContext)
        val correctInstances = listOf(
                StateEnd.True(DummyInstances.solveRequest, DummyInstances.executionStrategy),
                StateEnd.False(DummyInstances.solveRequest, DummyInstances.executionStrategy),
                StateEnd.Halt(DummyInstances.solveRequest, DummyInstances.executionStrategy, myException)
        )

        val startingSolutions = listOf(
                Solution.Yes(Truth.`true`(), Substitution.empty()),
                Solution.No(Truth.`true`()),
                Solution.Halt(Truth.`true`(), myException)
        )

        val toBeTested = startingSolutions.map { it.toStateEnd(DummyInstances.solveRequest, DummyInstances.executionStrategy) }

        correctInstances.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

}
