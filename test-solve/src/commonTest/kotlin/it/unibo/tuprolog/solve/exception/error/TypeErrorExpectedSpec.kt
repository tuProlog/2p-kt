package it.unibo.tuprolog.solve.exception.error

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.fail

/**
 * Test class for [TypeError.Expected]
 *
 * @author Enrico
 */
class TypeErrorExpectedSpec :
    FunSpec({
        val expectedTypes = TypeError.Expected.entries

        withTests(nameFn = { "to-term/${it.id}" }, expectedTypes) { expected ->
            assertEquals(Atom.of(expected.toString()), expected.toTerm())
        }

        withTests(nameFn = { "of-returns-predefined-instance/${it.id}" }, expectedTypes) { expected ->
            assertSame(expected, TypeError.Expected.of(expected.toString()))
            assertSame(expected, TypeError.Expected.of(expected.toString().uppercase()))
        }

        test("type-error-expected-of-throws-exception-if-name-is-unknown") {
            try {
                TypeError.Expected.of("dummy")
                fail()
            } catch (e: IllegalArgumentException) {
                // ok
            } catch (e: IllegalStateException) {
                // ok
            }
        }

        test("type-error-expected-constants-are-predefined-instances") {
            assertSame(TypeError.Expected.CALLABLE, TypeError.Expected.of("callable"))
        }

        withTests(nameFn = { "from-term-returns-predefined-instance/${it.id}" }, expectedTypes) { expected ->
            val name = expected.toString()
            assertSame(expected, TypeError.Expected.fromTerm(Atom.of(name))!!)
            assertSame(expected, TypeError.Expected.fromTerm(Atom.of(name.uppercase()))!!)
            assertSame(expected, TypeError.Expected.fromTerm(expected.toTerm())!!)
        }

        test("type-error-expected-enum-from-term-complains-for-incorrect-terms") {
            assertNull(TypeError.Expected.fromTerm(Struct.of("callable", Var.anonymous())))
            assertNull(TypeError.Expected.fromTerm(Var.of("CALLABLE")))
            try {
                TypeError.Expected.fromTerm(Atom.of("ciao"))
                fail()
            } catch (e: IllegalArgumentException) {
                // ok
            } catch (e: IllegalStateException) {
                // ok
            }
        }
    })

private val TypeError.Expected.id: String
    get() = name.lowercase().replace('_', '-')
