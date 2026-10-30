package it.unibo.tuprolog.solve.testutils

import io.kotest.core.spec.style.FunSpec
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
import kotlin.test.assertFailsWith

/**
 * Characterization tests for [assertSolutionEquals], pinning down what it compares (and what it deliberately
 * ignores) before the Kotest migration (#1017) rewrites it.
 */
class AssertSolutionEqualsSpec :
    FunSpec({
        val x = Var.of("X")

        val query = Struct.of("f", x)

        val context = DummyInstances.executionContext

        fun yesBindingXTo(term: Struct) = query.yes(Substitution.of(x, term))

        fun existenceError(name: String) = ExistenceError.forProcedure(context, Signature(name, 1))

        fun fails(block: () -> Unit) {
            assertFailsWith<AssertionError> { block() }
        }

        test("identical-yes-solutions-are-equal") {
            assertSolutionEquals(
                yesBindingXTo(Struct.of("g", Atom.of("a"))),
                yesBindingXTo(Struct.of("g", Atom.of("a"))),
            )
        }

        test("variables-in-substitution-values-are-compared-by-name-only") {
            assertSolutionEquals(yesBindingXTo(Struct.of("g", Var.of("Y"))), yesBindingXTo(Struct.of("g", Var.of("Y"))))
        }

        test("variables-in-substitution-values-with-different-names-differ") {
            fails {
                assertSolutionEquals(
                    yesBindingXTo(Struct.of("g", Var.of("Y"))),
                    yesBindingXTo(Struct.of("g", Var.of("Z"))),
                )
            }
        }

        test("yes-and-no-differ") {
            fails { assertSolutionEquals(yesBindingXTo(Struct.of("g", Atom.of("a"))), query.no()) }
        }

        test("halts-with-non-logic-errors-are-compared-by-exception-class-only") {
            assertSolutionEquals(
                query.halt(HaltException(message = "one", context = context)),
                query.halt(HaltException(message = "another", context = context)),
            )
        }

        test("halts-with-different-exception-classes-differ") {
            fails {
                assertSolutionEquals(
                    query.halt(existenceError("foo")),
                    query.halt(HaltException(context = context)),
                )
            }
        }

        test("halts-with-logic-errors-compare-their-error-structs") {
            assertSolutionEquals(query.halt(existenceError("foo")), query.halt(existenceError("foo")))
            fails { assertSolutionEquals(query.halt(existenceError("foo")), query.halt(existenceError("bar"))) }
        }

        test("solution-sequences-are-compared-in-order") {
            val a = yesBindingXTo(Struct.of("g", Atom.of("a")))
            val b = yesBindingXTo(Struct.of("g", Atom.of("b")))
            assertSolutionEquals(listOf(a, b), listOf(a, b))
            fails { assertSolutionEquals(listOf(a, b), listOf(b, a)) }
        }

        test("solution-sequences-preserve-multiplicity") {
            val a = yesBindingXTo(Struct.of("g", Atom.of("a")))
            fails { assertSolutionEquals(listOf(a, a), listOf(a)) }
            fails { assertSolutionEquals(listOf(a), listOf(a, a)) }
        }
    })
