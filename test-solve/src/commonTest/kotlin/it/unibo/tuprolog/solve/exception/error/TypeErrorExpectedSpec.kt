package it.unibo.tuprolog.solve.exception.error

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.exception.error.testutils.TypeErrorExpectedUtils
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
        test("type-error-expected-to-atom-works-as-expected") {
            TypeErrorExpectedUtils.allNamesToInstances.forEach { (name, instance) ->
                assertEquals(Atom.of(name), instance.toTerm())
            }
        }

        test("type-error-expected-of-returns-predefined-instance-if-name-correct") {
            TypeErrorExpectedUtils.predefinedErrorNamesToInstances.forEach { (name, instance) ->
                assertSame(instance, TypeError.Expected.of(name))
                assertSame(instance, TypeError.Expected.of(name.uppercase()))
            }
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

        test("type-error-expected-from-term-work-for-predefined-terms") {
            TypeErrorExpectedUtils.predefinedErrorNamesToInstances.forEach { (name, instance) ->
                assertSame(instance, TypeError.Expected.fromTerm(Atom.of(name))!!)
                assertSame(instance, TypeError.Expected.fromTerm(Atom.of(name.uppercase()))!!)
                assertSame(instance, TypeError.Expected.fromTerm(instance.toTerm())!!)
            }
        }

        test("type-error-expected-from-term-work-for-correct-terms") {
            for (e in TypeError.Expected.values()) {
                assertEquals(e, TypeError.Expected.fromTerm(e.toTerm()))
            }
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
