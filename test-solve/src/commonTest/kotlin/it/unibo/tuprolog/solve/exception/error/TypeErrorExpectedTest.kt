package it.unibo.tuprolog.solve.exception.error

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.exception.error.testutils.TypeErrorExpectedUtils
import kotlin.test.*

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
    fun typeErrorExpectedOfReturnsNewInstanceIfNameUnknown() {
        TypeErrorExpectedUtils.nonPredefinedToInstances.forEach { (name, instance) ->
            assertNotSame(instance, TypeError.Expected.of(name))
            assertNotSame(instance, TypeError.Expected.of(name.toUpperCase()))
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
        assertNotSame(TypeError.Expected.fromTerm(Atom.of("ciao")), TypeError.Expected.fromTerm(Atom.of("ciao")))
    }

    @Test
    fun typeErrorExpectedEnumFromTermComplainsForIncorrectTerms() {
        assertNull(TypeError.Expected.fromTerm(Struct.of("callable", Var.anonymous())))
        assertNull(TypeError.Expected.fromTerm(Var.of("CALLABLE")))
    }
}
