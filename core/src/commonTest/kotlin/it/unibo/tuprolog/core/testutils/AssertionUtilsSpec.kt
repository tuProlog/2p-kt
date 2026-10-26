package it.unibo.tuprolog.core.testutils

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertAllVsAll
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualsUsingVariablesSimpleNames
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNoEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNotEqualsUsingVariablesSimpleNames
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNotStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Characterization tests for [AssertionUtils], pinning down which of the three equality notions (plain,
 * structural, by variable simple names) each assertion checks before the Kotest migration (#1017) rewrites them.
 */
class AssertionUtilsSpec :
    FunSpec({
        val x = Var.of("X")

        test("same-variable-instance-satisfies-every-equality") {
            val term = Struct.of("f", x)
            assertEqualities(term, Struct.of("f", x))
        }

        test("fresh-variables-with-same-name-are-equal-only-structurally-and-by-simple-name") {
            val term1 = Struct.of("f", Var.of("X"))
            val term2 = Struct.of("f", Var.of("X"))
            assertStructurallyEquals(term1, term2)
            assertEqualsUsingVariablesSimpleNames(term1, term2)
            assertFailsWith<AssertionError> { assertEqualities(term1, term2) }
        }

        test("variables-with-different-names-are-structurally-equal-but-not-by-simple-name") {
            val term1 = Struct.of("f", Var.of("X"))
            val term2 = Struct.of("f", Var.of("Y"))
            assertStructurallyEquals(term1, term2)
            assertNotEqualsUsingVariablesSimpleNames(term1, term2)
            assertFailsWith<AssertionError> { assertEqualsUsingVariablesSimpleNames(term1, term2) }
        }

        test("different-functors-satisfy-no-equality") {
            val term1 = Struct.of("f", x)
            val term2 = Struct.of("g", x)
            assertNoEqualities(term1, term2)
            assertNotStructurallyEquals(term1, term2)
            assertFailsWith<AssertionError> { assertStructurallyEquals(term1, term2) }
            assertFailsWith<AssertionError> { assertNoEqualities(term1, term1) }
        }

        test("on-corresponding-items-visits-pairs-in-order") {
            val visited = mutableListOf<Pair<Int, String>>()
            onCorrespondingItems(listOf(1, 2), listOf("a", "b")) { i, s -> visited += i to s }
            assertEquals(listOf(1 to "a", 2 to "b"), visited)
        }

        test("on-corresponding-items-fails-on-size-mismatch-before-visiting-anything") {
            val visited = mutableListOf<Pair<Int, String>>()
            assertFailsWith<AssertionError> {
                onCorrespondingItems(listOf(1, 2, 3), listOf("a", "b")) { i, s -> visited += i to s }
            }
            assertFailsWith<AssertionError> {
                onCorrespondingItems(listOf(1), listOf("a", "b")) { i, s -> visited += i to s }
            }
            assertEquals(emptyList(), visited)
        }

        test("assert-all-vs-all-visits-every-ordered-pair-including-self-pairs") {
            val a = Atom.of("a")
            val b = Atom.of("b")
            val visited = mutableListOf<Pair<Atom, Atom>>()
            assertAllVsAll(listOf(a, b)) { l, r -> visited += l to r }
            assertEquals(listOf(a to a, a to b, b to a, b to b), visited)
        }
    })
