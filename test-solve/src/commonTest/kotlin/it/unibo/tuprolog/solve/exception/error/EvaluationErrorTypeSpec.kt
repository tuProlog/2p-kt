package it.unibo.tuprolog.solve.exception.error

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Test class for [EvaluationError.Type]
 *
 * @author Enrico
 */
class EvaluationErrorTypeSpec :
    FunSpec({
        test("evaluation-error-type-enum-to-atom-works-as-expected") {
            EvaluationError.Type.values().forEach {
                assertEquals(Atom.of(it.toString().lowercase()), it.toTerm())
            }
        }

        test("evaluation-error-type-enum-from-term-work-for-correct-terms") {
            assertEquals(EvaluationError.Type.INT_OVERFLOW, EvaluationError.Type.fromTerm(Atom.of("int_overflow"))!!)
            assertEquals(EvaluationError.Type.INT_OVERFLOW, EvaluationError.Type.fromTerm(Atom.of("INT_OVERFLOW"))!!)

            EvaluationError.Type.values().forEach {
                assertEquals(it, EvaluationError.Type.fromTerm(it.toTerm()))
            }
        }

        test("evaluation-error-type-enum-from-term-complains-for-incorrect-terms") {
            assertNull(EvaluationError.Type.fromTerm(Struct.of("underflow", Var.anonymous())))
            assertNull(EvaluationError.Type.fromTerm(Var.of("UNDERFLOW")))
            assertNull(EvaluationError.Type.fromTerm(Atom.of("ciao")))
        }
    })
