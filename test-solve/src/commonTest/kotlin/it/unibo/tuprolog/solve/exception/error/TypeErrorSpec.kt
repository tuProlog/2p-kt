package it.unibo.tuprolog.solve.exception.error

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.exception.testutils.LogicErrorUtils
import it.unibo.tuprolog.solve.exception.testutils.LogicErrorUtils.assertErrorStructCorrect
import kotlin.test.assertEquals

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
            assertEquals(correctTypeFunctor, TypeError.typeFunctor)
        }

        test("type-error-name-correct") {
            assertEquals(correctTypeFunctor, aCallableTypeError.type.functor)
        }

        test("type-error-type-correct") {
            assertEquals(
                Struct.of(correctTypeFunctor, testErrorType.toTerm(), callableTypeErrorActualValue),
                aCallableTypeError.type,
            )
        }

        test("type-error-to-throw-struct-correct") {
            assertErrorStructCorrect(aCallableTypeError)
        }

        test("type-error-expected-type-correct") {
            assertEquals(testErrorType, aCallableTypeError.expectedType)
        }

        test("type-error-actual-value-correct") {
            assertEquals(callableTypeErrorActualValue, aCallableTypeError.culprit)
        }
    })
