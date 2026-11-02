package it.unibo.tuprolog.solve.function

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.solve.function.testutils.ArithmeticEvaluatorUtils
import it.unibo.tuprolog.solve.function.testutils.ArithmeticEvaluatorUtils.errorCases
import it.unibo.tuprolog.solve.function.testutils.ArithmeticEvaluatorUtils.resultCases

/**
 * Test class for [ArithmeticEvaluator]
 *
 * @author Enrico
 */
class ArithmeticEvaluatorSpec :
    FunSpec({
        withTests(nameFn = { "throws-expected-errors/${it.id}" }, errorCases) { case ->
            val arithmeticEvaluator = ArithmeticEvaluator(ArithmeticEvaluatorUtils.commonFunctionsRequest)
            shouldThrowAny { case.input.accept(arithmeticEvaluator) }.shouldBeInstanceOf(case.errorType)
        }

        withTests(nameFn = { "computes-correctly-the-result/${it.id}" }, resultCases) { case ->
            val toBeTested = case.input.accept(ArithmeticEvaluator(ArithmeticEvaluatorUtils.commonFunctionsRequest))

            toBeTested::class shouldBe case.result::class
            toBeTested shouldBe case.result
        }
    })
