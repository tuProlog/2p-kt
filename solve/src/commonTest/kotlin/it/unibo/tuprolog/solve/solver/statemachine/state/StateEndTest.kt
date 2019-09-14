package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.testutils.DummyInstances
import it.unibo.tuprolog.solve.testutils.DummyInstances.executionStrategy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [StateEnd] and subclasses
 *
 * @author Enrico
 */
internal class StateEndTest {

    private val myScope = Scope.empty()
    private val solveRequest = Solve.Request(
            Signature("p", 2),
            listOf(myScope.varOf("A"), myScope.varOf("B")),
            DummyInstances.executionContext
    )

    @Test
    fun trueStateHoldInsertedData() {
        val toBeTested = StateEnd.True(solveRequest, executionStrategy)
        assertEquals(solveRequest, toBeTested.solveRequest)
    }

    @Test
    fun trueStateComputesCorrectlyAnswerSubstitution() {
        myScope.where {
            val toBeTested = StateEnd.True(
                    solveRequest.copy(context = with(solveRequest.context) {
                        copy(currentSubstitution = Substitution.of(
                                varOf("A") to varOf("C"),
                                varOf("C") to Atom.of("c"),
                                varOf("D") to Atom.of("d")
                        ))
                    }),
                    executionStrategy
            )

            assertEquals(Substitution.of(varOf("A"), Atom.of("c")), toBeTested.answerSubstitution)
        }
    }

    @Test
    fun falseStateHoldInsertedData() {
        val toBeTested = StateEnd.False(solveRequest, executionStrategy)
        assertEquals(solveRequest, toBeTested.solveRequest)
    }

    @Test
    fun haltStateHoldInsertedData() {
        val toBeTested = StateEnd.Halt(solveRequest, executionStrategy)
        assertEquals(solveRequest, toBeTested.solveRequest)
    }

    @Test
    fun timeoutStateHoldInsertedData() {
        val toBeTested = StateEnd.Timeout(solveRequest, executionStrategy)
        assertEquals(solveRequest, toBeTested.solveRequest)
    }

    @Test
    fun allStateEndInstancesReturnEmptyNextStatesSequence() {
        val toBeTested = listOf(
                StateEnd.True(solveRequest, executionStrategy),
                StateEnd.False(solveRequest, executionStrategy),
                StateEnd.Halt(solveRequest, executionStrategy),
                StateEnd.Timeout(solveRequest, executionStrategy)
        )

        toBeTested.forEach { assertTrue { it.behave().none() } }
    }

}
