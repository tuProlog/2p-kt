package it.unibo.tuprolog.solve.function

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.function.testutils.ExpressionEvaluatorUtils.createRequestWithFunctionBy
import it.unibo.tuprolog.solve.function.testutils.ExpressionEvaluatorUtils.noFunctionRequest
import it.unibo.tuprolog.solve.function.testutils.ExpressionEvaluatorUtils.reductionCases

/**
 * Test class for [ExpressionReducerTest]
 *
 * @author Enrico
 */
class ExpressionReducerSpec :
    FunSpec({
        withTests(nameFn = { "does-nothing-without-functions/${it.id}" }, reductionCases) { case ->
            case.input.accept(ExpressionReducer(noFunctionRequest)) shouldBe case.input
        }

        withTests(nameFn = { "applies-loaded-function/${it.id}" }, reductionCases) { case ->
            val req = createRequestWithFunctionBy(case.input.extractSignature(), case.function)

            case.input.accept(ExpressionReducer(req)) shouldBe case.output
        }
    })
