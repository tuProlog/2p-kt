package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.impl.AtomImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AtomUtils
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [Atom] companion object
 *
 * @author Enrico
 */
private class NotableAtomCase(
    val id: String,
    val value: String,
    val expected: Atom,
)

private val notableAtoms =
    listOf(
        NotableAtomCase("empty-list", "[]", EmptyList()),
        NotableAtomCase("empty-block", "{}", EmptyBlock()),
        NotableAtomCase("true", "true", Truth.TRUE),
        NotableAtomCase("fail", "fail", Truth.FAIL),
        NotableAtomCase("false", "false", Truth.FALSE),
    )

class AtomSpec :
    FunSpec({
        test("empty-set-atom-detected") {
            assertEqualities(Atom.of("{}"), Empty.block())
        }

        test("empty-list-atom-detected") {
            assertEqualities(Atom.of("[]"), Empty.list())
        }

        test("true-atom-detected") {
            assertEqualities(Atom.of("true"), Truth.TRUE)
        }

        test("false-atom-detected") {
            assertEqualities(Atom.of("false"), Truth.FALSE)
        }

        test("fail-atom-detected") {
            assertEqualities(Atom.of("fail"), Truth.FAIL)
        }

        withTests(nameFn = { "of/${it.id}" }, AtomUtils.nonSpecialAtomCases) { case ->
            assertEqualities(AtomImpl(case.value), Atom.of(case.value))
        }

        withTests(nameFn = { "of/notable/${it.id}" }, notableAtoms) { case ->
            assertEqualities(case.expected, Atom.of(case.value))
        }

        withTests(
            nameFn = { "is-functor-well-formed/${it.id}" },
            AtomUtils.mixedAtomCases.filter { it.value matches Atom.ATOM_PATTERN },
        ) { case ->
            assertTrue(Atom.of(case.value).isFunctorWellFormed)
        }

        test("escape-value/always-quotes") {
            assertEquals("'a'", Atom.escapeValue("a"))
        }

        test("escape-value-if-necessary/quotes-only-ill-formed-names") {
            assertEquals("a", Atom.escapeValueIfNecessary("a"))
            assertEquals("'A b'", Atom.escapeValueIfNecessary("A b"))
        }
    })
