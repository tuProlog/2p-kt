package it.unibo.tuprolog.solve.streams.solver

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.streams.testutils.SolverTestUtils.createSolveRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Test class for [StreamsExecutionContext]
 *
 * @author Enrico
 */
internal class ExecutionContextImplTest {
    @Test
    fun logicStackTraceCorrectlyComputed() {
        val sideEffectManagerWithLogicalParents =
            SideEffectManagerImpl(
                logicalParentRequests =
                    listOf(
                        createSolveRequest(Atom.of("ciao")),
                        createSolveRequest(Atom.of("ciao2")),
                    ),
            )
        val toBeTested = StreamsExecutionContext(sideEffectManager = sideEffectManagerWithLogicalParents)

        assertEquals(
            sequenceOf(Atom.of("ciao"), Atom.of("ciao2")).toList(),
            toBeTested.logicStackTrace.toList(),
        )
    }

    @Test
    fun getSideEffectManagerWorksForCorrectInstances() {
        val aSideEffectManager = SideEffectManagerImpl()
        val contextImpl: ExecutionContext = StreamsExecutionContext(sideEffectManager = aSideEffectManager)

        assertEquals(aSideEffectManager, contextImpl.getSideEffectManager())
    }

    @Test
    fun getSideEffectManagerReturnsNulIfNotCorrectExecutionContextInstance() {
        assertNull(DummyInstances.executionContext.getSideEffectManager())
    }
}
