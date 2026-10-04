package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.assertExamplesHold
import it.unibo.tuprolog.solve.assertFullyDocumented
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.library.Runtime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

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
    fun argumentErrorsAreNotWrappedIntoSystemErrors() {
        val solver = Solver.prolog.solverWithDefaultBuiltins(otherLibraries = Runtime.of(OOPLib))
        val solution = solver.solveOnce(Struct.of("type", Var.of("Name"), Atom.of("java.lang.String")))
        assertIs<TypeError>((solution as? Solution.Halt)?.exception, "$solution")
    }

    @Test
    fun documentationExamplesHold() {
        assertExamplesHold(OOPLib, skip = emptySet()) {
            Solver.prolog.solverWithDefaultBuiltins(otherLibraries = Runtime.of(OOPLib))
        }
    }
}
