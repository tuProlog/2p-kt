package it.unibo.tuprolog.primitive.function

import it.unibo.tuprolog.primitive.function.testutils.FunctionWrapperUtils.createFunctionRequest
import it.unibo.tuprolog.primitive.function.testutils.FunctionWrapperUtils.defaultFunctionResult
import it.unibo.tuprolog.primitive.function.testutils.FunctionWrapperUtils.function
import it.unibo.tuprolog.primitive.testutils.WrapperUtils.allSignatures
import it.unibo.tuprolog.primitive.testutils.WrapperUtils.wrapperToMatchingSignatureRequest
import it.unibo.tuprolog.primitive.testutils.WrapperUtils.wrapperToNotMatchingSignatureRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [PrologFunction]
 *
 * @author Enrico
 */
internal class PrologFunctionTest {

    @Test
    fun functionOfReturnsPrologFunctionBehavingExactlyAsProvidedOne() {
        wrapperToMatchingSignatureRequest(::functionOf, function, ::createFunctionRequest).zip(allSignatures)
            .forEach { (functionToGoodRequests, functionSignature) ->
                val (checkedFunction, goodRequests) = functionToGoodRequests
                goodRequests.forEach {
                    if (functionSignature.vararg) return
                    assertEquals(defaultFunctionResult, checkedFunction(it))
                }
            }
        // TODO delete above test and enable the code below after solving TODO in "Signature"
//        wrapperToMatchingSignatureRequest(::functionOf, function, ::createRequest).forEach { (checkedFunction, goodRequests) ->
//            goodRequests.forEach {
//                assertEquals(defaultFunctionResult, checkedFunction(it))
//            }
//        }
    }

    @Test
    fun functionOfComplainsIfDifferentRequestSignatureIsDetected() {
        wrapperToNotMatchingSignatureRequest(::functionOf, function, ::createFunctionRequest)
            .forEach { (checkedFunction, badRequests) ->
                badRequests.forEach {
                    assertFailsWith<IllegalArgumentException> { checkedFunction(it) }
                }
            }
    }

}
