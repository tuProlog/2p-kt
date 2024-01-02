package it.unibo.tuprolog.solve.function

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.function.testutils.ExpressionEvaluatorUtils.createRequestWithFunctionBy
import it.unibo.tuprolog.solve.function.testutils.ExpressionEvaluatorUtils.inputFunctionOutputTriple
import it.unibo.tuprolog.solve.function.testutils.ExpressionEvaluatorUtils.noFunctionRequest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [ExpressionReducerTest]
 *
 * @author Enrico
 */
internal class ExpressionReducerTest {
    @Test
    fun doesNothingIfGivenExecutionContextDoesNotDefineFunctions() {
        inputFunctionOutputTriple.forEach { (input, _, _) ->
            assertEquals(input, input.accept(ExpressionReducer(noFunctionRequest)))
        }
    }

    @Test
    fun appliesCorrectlyTransformationIfFunctionLoaded() {
        inputFunctionOutputTriple.forEach { (input, function, output) ->
            val req = createRequestWithFunctionBy((input as Struct).extractSignature(), function)
            val toBeTested = input.accept(ExpressionReducer(req))

            assertEquals(output, toBeTested)
        }
    }
}
