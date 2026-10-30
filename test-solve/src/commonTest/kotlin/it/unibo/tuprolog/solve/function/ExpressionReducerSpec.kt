package it.unibo.tuprolog.solve.function

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.function.testutils.ExpressionEvaluatorUtils.createRequestWithFunctionBy
import it.unibo.tuprolog.solve.function.testutils.ExpressionEvaluatorUtils.inputFunctionOutputTriple
import it.unibo.tuprolog.solve.function.testutils.ExpressionEvaluatorUtils.noFunctionRequest
import kotlin.test.assertEquals

/**
 * Test class for [ExpressionReducerTest]
 *
 * @author Enrico
 */
class ExpressionReducerSpec :
    FunSpec({
        test("does-nothing-if-given-execution-context-does-not-define-functions") {
            inputFunctionOutputTriple.forEach { (input, _, _) ->
                assertEquals(input, input.accept(ExpressionReducer(noFunctionRequest)))
            }
        }

        test("applies-correctly-transformation-if-function-loaded") {
            inputFunctionOutputTriple.forEach { (input, function, output) ->
                val req = createRequestWithFunctionBy((input as Struct).extractSignature(), function)
                val toBeTested = input.accept(ExpressionReducer(req))

                assertEquals(output, toBeTested)
            }
        }
    })
