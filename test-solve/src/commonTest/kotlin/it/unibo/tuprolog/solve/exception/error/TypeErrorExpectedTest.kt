package it.unibo.tuprolog.solve.exception.error

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.exception.error.testutils.TypeErrorExpectedUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.fail

/**
 * Test class for [TypeError.Expected]
 *
 * @author Enrico
 */
internal class TypeErrorExpectedTest {

    @Test
    fun typeErrorExpectedToAtomWorksAsExpected() {
        TypeErrorExpectedUtils.allNamesToInstances.forEach { (name, instance) ->
            assertEquals(Atom.of(name), instance.toTerm())
        }
    }

    @Test
    fun typeErrorExpectedOfReturnsPredefinedInstanceIfNameCorrect() {
        TypeErrorExpectedUtils.predefinedErrorNamesToInstances.forEach { (name, instance) ->
            assertSame(instance, TypeError.Expected.of(name))
            assertSame(instance, TypeError.Expected.of(name.toUpperCase()))
        }
    }

    @Test
    fun typeErrorExpectedOfThrowsExceptionIfNameIsUnknown() {
        try {
            TypeError.Expected.of("dummy")
            fail()
        } catch (e: IllegalArgumentException) {
            // ok
        } catch (e: IllegalStateException) {
            // ok
        }
    }

    @Test
    fun typeErrorExpectedConstantsArePredefinedInstances() {
        assertSame(TypeError.Expected.CALLABLE, TypeError.Expected.of("callable"))
    }

    @Test
    fun typeErrorExpectedFromTermWorkForPredefinedTerms() {
        TypeErrorExpectedUtils.predefinedErrorNamesToInstances.forEach { (name, instance) ->
            assertSame(instance, TypeError.Expected.fromTerm(Atom.of(name))!!)
            assertSame(instance, TypeError.Expected.fromTerm(Atom.of(name.toUpperCase()))!!)
            assertSame(instance, TypeError.Expected.fromTerm(instance.toTerm())!!)
        }
    }

    @Test
    fun typeErrorExpectedFromTermWorkForCorrectTerms() {
        for (e in TypeError.Expected.values()) {
            assertEquals(e, TypeError.Expected.fromTerm(e.toTerm()))
        }
    }

    @Test
    fun typeErrorExpectedEnumFromTermComplainsForIncorrectTerms() {
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
}
