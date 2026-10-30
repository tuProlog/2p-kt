package it.unibo.tuprolog.solve.function

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.function.testutils.ArithmeticEvaluatorUtils
import it.unibo.tuprolog.solve.function.testutils.ArithmeticEvaluatorUtils.errorCases
import it.unibo.tuprolog.solve.function.testutils.ArithmeticEvaluatorUtils.resultCases
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [ArithmeticEvaluator]
 *
 * @author Enrico
 */
class ArithmeticEvaluatorSpec :
    FunSpec({
        withTests(nameFn = { "throws-expected-errors/${it.id}" }, errorCases) { case ->
            val arithmeticEvaluator = ArithmeticEvaluator(ArithmeticEvaluatorUtils.commonFunctionsRequest)
            assertFailsWith(case.errorType) { case.input.accept(arithmeticEvaluator) }
        }

        withTests(nameFn = { "computes-correctly-the-result/${it.id}" }, resultCases) { case ->
            val toBeTested = case.input.accept(ArithmeticEvaluator(ArithmeticEvaluatorUtils.commonFunctionsRequest))

            assertEquals(case.result::class, toBeTested::class)
            assertEquals(case.result, toBeTested)
        }
    })
