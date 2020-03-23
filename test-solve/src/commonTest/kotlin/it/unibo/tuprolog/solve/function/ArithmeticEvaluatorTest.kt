package it.unibo.tuprolog.solve.function

import it.unibo.tuprolog.solve.function.testutils.ArithmeticEvaluatorUtils
import it.unibo.tuprolog.solve.function.testutils.ArithmeticEvaluatorUtils.inputToErrorType
import it.unibo.tuprolog.solve.function.testutils.ArithmeticEvaluatorUtils.inputToResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [ArithmeticEvaluator]
 *
 * @author Enrico
 */
internal class ArithmeticEvaluatorTest {

    private val arithmeticEvaluator = ArithmeticEvaluator(ArithmeticEvaluatorUtils.commonFunctionsContext)

    @Test
    fun throwsExpectedErrors() {
        inputToErrorType.forEach { (input, errorType) ->
            assertFailsWith(errorType) { input.accept(arithmeticEvaluator) }
        }
    }

    @Test
    fun computesCorrectlyTheResult() {
        inputToResult.forEach { (input, result) ->
            val toBeTested = input.accept(arithmeticEvaluator)

            assertEquals(result::class, toBeTested::class)
            assertEquals(result, toBeTested)
        }
    }

}
