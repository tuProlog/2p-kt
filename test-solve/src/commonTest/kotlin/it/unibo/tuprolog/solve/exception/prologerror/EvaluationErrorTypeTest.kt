package it.unibo.tuprolog.solve.exception.prologerror

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Test class for [EvaluationError.Type]
 *
 * @author Enrico
 */
internal class EvaluationErrorTypeTest {

    @Test
    fun evaluationErrorTypeEnumToAtomWorksAsExpected() {
        EvaluationError.Type.values().forEach {
            assertEquals(Atom.of(it.toString().toLowerCase()), it.toTerm())
        }
    }

    @Test
    fun evaluationErrorTypeEnumFromTermWorkForCorrectTerms() {
        assertEquals(EvaluationError.Type.INT_OVERFLOW, EvaluationError.Type.fromTerm(Atom.of("int_overflow"))!!)
        assertEquals(EvaluationError.Type.INT_OVERFLOW, EvaluationError.Type.fromTerm(Atom.of("INT_OVERFLOW"))!!)

        EvaluationError.Type.values().forEach {
            assertEquals(it, EvaluationError.Type.fromTerm(it.toTerm()))
        }
    }

    @Test
    fun evaluationErrorTypeEnumFromTermComplainsForIncorrectTerms() {
        assertNull(EvaluationError.Type.fromTerm(Struct.of("underflow", Var.anonymous())))
        assertNull(EvaluationError.Type.fromTerm(Var.of("UNDERFLOW")))
        assertNull(EvaluationError.Type.fromTerm(Atom.of("ciao")))
    }
}
