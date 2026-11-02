package it.unibo.tuprolog.solve.exception.error

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var

/**
 * Test class for [EvaluationError.Type]
 *
 * @author Enrico
 */
class EvaluationErrorTypeSpec :
    FunSpec({
        val types = EvaluationError.Type.entries

        withTests(nameFn = { "to-term/${it.id}" }, types) { type ->
            type.toTerm() shouldBe Atom.of(type.toString().lowercase())
        }

        test("from-term/case-insensitive-name") {
            EvaluationError.Type.fromTerm(Atom.of("int_overflow"))!! shouldBe EvaluationError.Type.INT_OVERFLOW
            EvaluationError.Type.fromTerm(Atom.of("INT_OVERFLOW"))!! shouldBe EvaluationError.Type.INT_OVERFLOW
        }

        withTests(nameFn = { "from-term/${it.id}" }, types) { type ->
            EvaluationError.Type.fromTerm(type.toTerm()) shouldBe type
        }

        test("evaluation-error-type-enum-from-term-complains-for-incorrect-terms") {
            EvaluationError.Type.fromTerm(Struct.of("underflow", Var.anonymous())).shouldBeNull()
            EvaluationError.Type.fromTerm(Var.of("UNDERFLOW")).shouldBeNull()
            EvaluationError.Type.fromTerm(Atom.of("ciao")).shouldBeNull()
        }
    })

private val EvaluationError.Type.id: String
    get() = name.lowercase().replace('_', '-')
