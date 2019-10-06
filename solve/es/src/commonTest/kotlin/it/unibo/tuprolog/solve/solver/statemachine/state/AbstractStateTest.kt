package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.testutils.DummyInstances.executionStrategy
import it.unibo.tuprolog.solve.testutils.DummyInstances.solveRequest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [AbstractState]
 *
 * @author Enrico
 */
internal class AbstractStateTest {

    /** An [AbstractState] instance with dummy parameters, and emptySequence returning behaviour */
    private val abstractStateUnderTest = object : AbstractState(solveRequest, executionStrategy) {
        override fun behave(): Sequence<State> = emptySequence()
    }

    @Test
    fun holdsInsertedData() {
        assertEquals(solveRequest, abstractStateUnderTest.solve)
    }

    @Test
    fun hasBehavedDefaultsToFalse() {
        assertEquals(false, abstractStateUnderTest.hasBehaved)
    }

    @Test
    fun naiveImplementationIsImmutable() {
        abstractStateUnderTest.behave()
        assertEquals(false, abstractStateUnderTest.hasBehaved)
    }

    @Test
    fun behaveWorksAsExpected() {
        assertEquals(emptySequence(), abstractStateUnderTest.behave())
    }
}
