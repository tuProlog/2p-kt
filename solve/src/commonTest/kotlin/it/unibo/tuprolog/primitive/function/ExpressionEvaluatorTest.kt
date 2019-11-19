package it.unibo.tuprolog.primitive.function

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.primitive.function.testutils.ExpressionEvaluatorUtils
import it.unibo.tuprolog.primitive.function.testutils.ExpressionEvaluatorUtils.createContextWithFunctionBy
import it.unibo.tuprolog.primitive.function.testutils.ExpressionEvaluatorUtils.inputFunctionOutputTriple
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

/**
 * Test class for [ExpressionEvaluator]
 *
 * @author Enrico
 */
internal class ExpressionEvaluatorTest {

    private val identityExpressionEvaluator = ExpressionEvaluator(ExpressionEvaluatorUtils.noFunctionsContext)

    @Test
    fun doesNothingIfGivenExecutionContextDoesNotDefineFunctions() {
        inputFunctionOutputTriple.forEach { (input, _, _) ->
            assertSame(input, input.accept(identityExpressionEvaluator))
        }
    }

    @Test
    fun appliesCorrectlyTransformationIfFunctionLoaded() {
        inputFunctionOutputTriple.forEach { (input, function, output) ->
            val context = createContextWithFunctionBy((input as Struct).extractSignature(), function)
            val toBeTested = input.accept(ExpressionEvaluator(context))

            assertEquals(output, toBeTested)
        }
    }

}
