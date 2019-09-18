package it.unibo.tuprolog.solve.exception.prologerror

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Test class for [TypeError.Expected]
 *
 * @author Enrico
 */
internal class TypeErrorExpectedTest {

    @Test
    fun typeErrorExpectedEnumToAtomWorksAsExpected() {
        TypeError.Expected.values().forEach {
            assertEquals(Atom.of(it.toString().toLowerCase()), it.toAtom())
        }
    }

    @Test
    fun typeErrorExpectedEnumFromTermWorkForCorrectTerms() {
        assertEquals(TypeError.Expected.CALLABLE, TypeError.Expected.fromTerm(Atom.of("callable"))!!)
        assertEquals(TypeError.Expected.CALLABLE, TypeError.Expected.fromTerm(Atom.of("CALLABLE"))!!)

        TypeError.Expected.values().forEach {
            assertEquals(it, TypeError.Expected.fromTerm(it.toAtom()))
        }
    }

    @Test
    fun typeErrorExpectedEnumFromTermComplainsForIncorrectTerms() {
        assertNull(TypeError.Expected.fromTerm(Struct.of("callable", Var.anonymous())))
        assertNull(TypeError.Expected.fromTerm(Var.of("CALLABLE")))
        assertNull(TypeError.Expected.fromTerm(Atom.of("ciao")))
    }
}
