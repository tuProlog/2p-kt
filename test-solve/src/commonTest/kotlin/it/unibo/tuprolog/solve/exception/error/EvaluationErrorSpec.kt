package it.unibo.tuprolog.solve.exception.error

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.exception.testutils.LogicErrorUtils
import it.unibo.tuprolog.solve.exception.testutils.LogicErrorUtils.assertErrorStructCorrect

/**
 * Test class for [EvaluationError]
 *
 * @author Enrico
 */
class EvaluationErrorSpec :
    FunSpec({
        val testErrorType = EvaluationError.Type.FLOAT_OVERFLOW

        val aCallableTypeError =
            EvaluationError(
                context = LogicErrorUtils.aContext,
                errorType = testErrorType,
            )

        val correctTypeFunctor = "evaluation_error"

        test("evaluation-error-type-struct-functor-correct") {
            EvaluationError.typeFunctor shouldBe correctTypeFunctor
        }

        test("evaluation-error-name-correct") {
            aCallableTypeError.type.functor shouldBe correctTypeFunctor
        }

        test("evaluation-error-type-correct") {
            aCallableTypeError.type shouldBe Struct.of(correctTypeFunctor, testErrorType.toTerm())
        }

        test("evaluation-error-to-throw-struct-correct") {
            assertErrorStructCorrect(aCallableTypeError)
        }

        test("evaluation-error-error-type-correct") {
            aCallableTypeError.errorType shouldBe testErrorType
        }
    })
