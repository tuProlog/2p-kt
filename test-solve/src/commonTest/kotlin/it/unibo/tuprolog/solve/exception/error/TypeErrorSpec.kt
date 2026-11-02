package it.unibo.tuprolog.solve.exception.error

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.exception.testutils.LogicErrorUtils
import it.unibo.tuprolog.solve.exception.testutils.LogicErrorUtils.assertErrorStructCorrect

/**
 * Test class for [TypeError]
 *
 * @author Enrico
 */
class TypeErrorSpec :
    FunSpec({
        val callableTypeErrorActualValue = Integer.of(1)

        val testErrorType = TypeError.Expected.CALLABLE

        val aCallableTypeError =
            TypeError(
                context = LogicErrorUtils.aContext,
                expectedType = testErrorType,
                culprit = callableTypeErrorActualValue,
            )

        val correctTypeFunctor = "type_error"

        test("type-error-type-struct-functor-correct") {
            TypeError.typeFunctor shouldBe correctTypeFunctor
        }

        test("type-error-name-correct") {
            aCallableTypeError.type.functor shouldBe correctTypeFunctor
        }

        test("type-error-type-correct") {
            aCallableTypeError.type shouldBe
                Struct.of(correctTypeFunctor, testErrorType.toTerm(), callableTypeErrorActualValue)
        }

        test("type-error-to-throw-struct-correct") {
            assertErrorStructCorrect(aCallableTypeError)
        }

        test("type-error-expected-type-correct") {
            aCallableTypeError.expectedType shouldBe testErrorType
        }

        test("type-error-actual-value-correct") {
            aCallableTypeError.culprit shouldBe callableTypeErrorActualValue
        }
    })
