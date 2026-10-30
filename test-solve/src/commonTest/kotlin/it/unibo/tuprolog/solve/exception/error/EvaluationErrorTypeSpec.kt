package it.unibo.tuprolog.solve.exception.error

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
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
        val types = EvaluationError.Type.entries

        withTests(nameFn = { "to-term/${it.id}" }, types) { type ->
            assertEquals(Atom.of(type.toString().lowercase()), type.toTerm())
        }

        test("from-term/case-insensitive-name") {
            assertEquals(EvaluationError.Type.INT_OVERFLOW, EvaluationError.Type.fromTerm(Atom.of("int_overflow"))!!)
            assertEquals(EvaluationError.Type.INT_OVERFLOW, EvaluationError.Type.fromTerm(Atom.of("INT_OVERFLOW"))!!)
        }

        withTests(nameFn = { "from-term/${it.id}" }, types) { type ->
            assertEquals(type, EvaluationError.Type.fromTerm(type.toTerm()))
        }

        test("evaluation-error-type-enum-from-term-complains-for-incorrect-terms") {
            assertNull(EvaluationError.Type.fromTerm(Struct.of("underflow", Var.anonymous())))
            assertNull(EvaluationError.Type.fromTerm(Var.of("UNDERFLOW")))
            assertNull(EvaluationError.Type.fromTerm(Atom.of("ciao")))
        }
    })

private val EvaluationError.Type.id: String
    get() = name.lowercase().replace('_', '-')
