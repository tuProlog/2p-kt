package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.testutils.DummyInstances
import it.unibo.tuprolog.solve.testutils.DummyInstances.executionStrategy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
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
    private val anException = HaltException(context = DummyInstances.executionContext)

    /** A list with all state types instances */
    private val allStateList = listOf(
            StateEnd.True(solveRequest, executionStrategy),
            StateEnd.False(solveRequest, executionStrategy),
            StateEnd.Halt(solveRequest, executionStrategy, anException)
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
                        ) as Substitution.Unifier)
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
        val toBeTested = StateEnd.Halt(solveRequest, executionStrategy, anException)
        assertEquals(solveRequest, toBeTested.solveRequest)
        assertEquals(anException, toBeTested.exception)
    }

    @Test
    fun allStateEndInstancesReturnEmptyNextStatesSequence() {
        allStateList.forEach { assertTrue { it.behave().none() } }
    }

    @Test
    fun makeCopyRedirectsCallToCorrectCopyMethod() {
        // precondition
        allStateList.forEach { it.solveRequest != DummyInstances.solveRequest }

        val toBeTested = allStateList.map { it.makeCopy(DummyInstances.solveRequest) }

        allStateList.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected::class, actual::class) }
        toBeTested.forEach { assertSame(DummyInstances.solveRequest, it.solveRequest) }
    }
}
