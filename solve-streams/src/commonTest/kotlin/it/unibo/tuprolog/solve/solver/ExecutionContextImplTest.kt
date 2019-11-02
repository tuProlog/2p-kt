package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.solve.solver.testutils.SolverTestUtils.createSolveRequest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [ExecutionContextImpl]
 *
 * @author Enrico
 */
internal class ExecutionContextImplTest {

    @Test
    fun prologStackTraceCorrectlyComputed() {
        val sideEffectManagerWithLogicalParents = SideEffectManagerImpl(
                logicalParentRequests = sequenceOf(
                        createSolveRequest(Atom.of("ciao")),
                        createSolveRequest(Atom.of("ciao2"))
                )
        )
        val toBeTested = ExecutionContextImpl(sideEffectManager = sideEffectManagerWithLogicalParents)

        assertEquals(
                sequenceOf(Atom.of("ciao"), Atom.of("ciao2")).toList(),
                toBeTested.prologStackTrace.toList()
        )
    }

}
