package it.unibo.tuprolog.solve.streams.solver.fsm

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.solve.streams.solver.StreamsExecutionContext
import it.unibo.tuprolog.solve.streams.testutils.SolverTestUtils.createSolveRequest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [AbstractState]
 *
 * @author Enrico
 */
internal class AbstractStateTest {

    private val solveRequest = createSolveRequest(Atom.of("test"))

    /** An [AbstractState] instance with dummy parameters, and emptySequence returning behaviour */
    private val abstractStateUnderTest = object : AbstractState(solveRequest) {
        override fun behave(): Sequence<State> = emptySequence()
        override val context: StreamsExecutionContext = solveRequest.context
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
