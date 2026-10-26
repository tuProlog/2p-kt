package it.unibo.tuprolog.solve.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * Characterization tests for [assertSolutionEquals], pinning down what it compares (and what it deliberately
 * ignores) before the Kotest migration (#1017) rewrites it.
 */
internal class AssertSolutionEqualsTest {
    private val x = Var.of("X")
    private val query = Struct.of("f", x)
    private val context = DummyInstances.executionContext

    private fun yesBindingXTo(term: Struct) = query.yes(Substitution.of(x, term))

    private fun existenceError(name: String) = ExistenceError.forProcedure(context, Signature(name, 1))

    private fun fails(block: () -> Unit) {
        assertFailsWith<AssertionError> { block() }
    }

    @Test
    fun identicalYesSolutionsAreEqual() {
        assertSolutionEquals(yesBindingXTo(Struct.of("g", Atom.of("a"))), yesBindingXTo(Struct.of("g", Atom.of("a"))))
    }

    @Test
    fun variablesInSubstitutionValuesAreComparedByNameOnly() {
        assertSolutionEquals(yesBindingXTo(Struct.of("g", Var.of("Y"))), yesBindingXTo(Struct.of("g", Var.of("Y"))))
    }

    @Test
    fun variablesInSubstitutionValuesWithDifferentNamesDiffer() {
        fails {
            assertSolutionEquals(
                yesBindingXTo(Struct.of("g", Var.of("Y"))),
                yesBindingXTo(Struct.of("g", Var.of("Z"))),
            )
        }
    }

    @Test
    fun yesAndNoDiffer() {
        fails { assertSolutionEquals(yesBindingXTo(Struct.of("g", Atom.of("a"))), query.no()) }
    }

    @Test
    fun haltsWithNonLogicErrorsAreComparedByExceptionClassOnly() {
        assertSolutionEquals(
            query.halt(HaltException(message = "one", context = context)),
            query.halt(HaltException(message = "another", context = context)),
        )
    }

    @Test
    fun haltsWithDifferentExceptionClassesDiffer() {
        fails {
            assertSolutionEquals(
                query.halt(existenceError("foo")),
                query.halt(HaltException(context = context)),
            )
        }
    }

    @Test
    fun haltsWithLogicErrorsCompareTheirErrorStructs() {
        assertSolutionEquals(query.halt(existenceError("foo")), query.halt(existenceError("foo")))
        fails { assertSolutionEquals(query.halt(existenceError("foo")), query.halt(existenceError("bar"))) }
    }

    @Test
    fun solutionSequencesAreComparedInOrder() {
        val a = yesBindingXTo(Struct.of("g", Atom.of("a")))
        val b = yesBindingXTo(Struct.of("g", Atom.of("b")))
        assertSolutionEquals(listOf(a, b), listOf(a, b))
        fails { assertSolutionEquals(listOf(a, b), listOf(b, a)) }
    }

    @Test
    fun solutionSequencesPreserveMultiplicity() {
        val a = yesBindingXTo(Struct.of("g", Atom.of("a")))
        fails { assertSolutionEquals(listOf(a, a), listOf(a)) }
        fails { assertSolutionEquals(listOf(a), listOf(a, a)) }
    }
}
