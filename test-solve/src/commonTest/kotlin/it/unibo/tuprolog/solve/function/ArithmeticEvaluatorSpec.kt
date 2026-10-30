package it.unibo.tuprolog.solve.function

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.function.testutils.ArithmeticEvaluatorUtils
import it.unibo.tuprolog.solve.function.testutils.ArithmeticEvaluatorUtils.inputToErrorType
import it.unibo.tuprolog.solve.function.testutils.ArithmeticEvaluatorUtils.inputToResult
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [ArithmeticEvaluator]
 *
 * @author Enrico
 */
class ArithmeticEvaluatorSpec :
    FunSpec({
        val arithmeticEvaluator = ArithmeticEvaluator(ArithmeticEvaluatorUtils.commonFunctionsRequest)

        test("throws-expected-errors") {
            inputToErrorType.forEach { (input, errorType) ->
                assertFailsWith(errorType) { input.accept(arithmeticEvaluator) }
            }
        }

        test("computes-correctly-the-result") {
            inputToResult.forEach { (input, result) ->
                val toBeTested = input.accept(arithmeticEvaluator)

                assertEquals(result::class, toBeTested::class)
                assertEquals(result, toBeTested)
            }
        }
    })
