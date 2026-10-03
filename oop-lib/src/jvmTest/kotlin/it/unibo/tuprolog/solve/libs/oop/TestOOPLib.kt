package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.assertExamplesHold
import it.unibo.tuprolog.solve.assertFullyDocumented
import it.unibo.tuprolog.solve.library.Runtime
import kotlin.test.Test
import kotlin.test.assertEquals

class TestOOPLib {
    @Test
    fun testSelfEquality() {
        assertEquals(OOPLib, OOPLib)
    }

    @Test
    fun testItemEquality() {
        assertEquals(Runtime.of(OOPLib), Runtime.of(OOPLib))
    }

    @Test
    fun everyItemIsDocumented() {
        assertFullyDocumented(OOPLib)
    }

    @Test
    fun documentationExamplesHold() {
        assertExamplesHold(OOPLib, skip = emptySet()) {
            Solver.prolog.solverWithDefaultBuiltins(otherLibraries = Runtime.of(OOPLib))
        }
    }
}
