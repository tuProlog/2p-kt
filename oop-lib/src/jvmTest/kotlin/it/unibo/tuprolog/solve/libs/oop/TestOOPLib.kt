package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.assertExamplesHold
import it.unibo.tuprolog.solve.assertFullyDocumented
import it.unibo.tuprolog.solve.exception.error.ExistenceError
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
    fun callsMatchingNoOverloadRaiseExistenceErrors() {
        val solver = Solver.prolog.solverWithDefaultBuiltins(otherLibraries = Runtime.of(OOPLib))
        val tooManyArguments = List.of((1..3).map { Integer.of(it) })
        val list = Var.of("L")
        val goals =
            listOf(
                Struct.of("new_object", Atom.of("java.util.ArrayList"), tooManyArguments, list),
                Struct.of(
                    ",",
                    Struct.of("new_object", Atom.of("java.util.ArrayList"), List.empty(), list),
                    Struct.of("invoke_method", list, Struct.of("add", tooManyArguments.toList()), Var.of("R")),
                ),
            )
        for (goal in goals) {
            val solution = solver.solveOnce(goal)
            assertIs<ExistenceError>((solution as? Solution.Halt)?.exception, "$solution")
        }
    }

    @Test
    fun documentationExamplesHold() {
        assertExamplesHold(OOPLib, skip = emptySet()) {
            Solver.prolog.solverWithDefaultBuiltins(otherLibraries = Runtime.of(OOPLib))
        }
    }
}
