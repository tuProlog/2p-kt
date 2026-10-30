package it.unibo.tuprolog.solve.exception.error

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.exception.testutils.LogicErrorUtils
import it.unibo.tuprolog.solve.exception.testutils.LogicErrorUtils.assertErrorStructCorrect
import kotlin.test.assertEquals

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
            assertEquals(correctTypeFunctor, EvaluationError.typeFunctor)
        }

        test("evaluation-error-name-correct") {
            assertEquals(correctTypeFunctor, aCallableTypeError.type.functor)
        }

        test("evaluation-error-type-correct") {
            assertEquals(
                Struct.of(correctTypeFunctor, testErrorType.toTerm()),
                aCallableTypeError.type,
            )
        }

        test("evaluation-error-to-throw-struct-correct") {
            assertErrorStructCorrect(aCallableTypeError)
        }

        test("evaluation-error-error-type-correct") {
            assertEquals(testErrorType, aCallableTypeError.errorType)
        }
    })
