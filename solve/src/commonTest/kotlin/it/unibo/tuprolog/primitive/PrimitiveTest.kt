package it.unibo.tuprolog.primitive

import it.unibo.tuprolog.primitive.testutils.PrimitiveWrapperUtils.createPrimitiveRequest
import it.unibo.tuprolog.primitive.testutils.PrimitiveWrapperUtils.defaultPrimitiveResult
import it.unibo.tuprolog.primitive.testutils.PrimitiveWrapperUtils.primitive
import it.unibo.tuprolog.primitive.testutils.WrapperUtils.allSignatures
import it.unibo.tuprolog.primitive.testutils.WrapperUtils.wrapperToMatchingSignatureRequest
import it.unibo.tuprolog.primitive.testutils.WrapperUtils.wrapperToNotMatchingSignatureRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [Primitive] companion functions
 *
 * @author Enrico
 */
internal class PrimitiveTest {

    @Test
    fun primitiveOfReturnsPrimitiveBehavingExactlyAsProvidedOne() {
        wrapperToMatchingSignatureRequest(::primitiveOf, primitive, ::createPrimitiveRequest).zip(allSignatures)
            .forEach { (primitiveToGoodRequests, primitiveSignature) ->
                val (checkedPrimitive, goodRequests) = primitiveToGoodRequests
                goodRequests.forEach {
                    if (primitiveSignature.vararg) return
                    assertEquals(defaultPrimitiveResult, checkedPrimitive(it))
                }
            }
        // TODO delete above test and enable the code below after solving TODO in "Signature"
//        wrapperToMatchingSignatureRequest(::primitiveOf, primitive, ::createRequest).forEach { (checkedPrimitive, goodRequests) ->
//            goodRequests.forEach {
//                assertEquals(defaultPrimitiveResult, checkedPrimitive(it))
//            }
//        }
    }

    @Test
    fun primitiveOfComplainsIfDifferentRequestSignatureIsDetected() {
        wrapperToNotMatchingSignatureRequest(::primitiveOf, primitive, ::createPrimitiveRequest)
            .forEach { (checkedPrimitive, badRequests) ->
                badRequests.forEach {
                    assertFailsWith<IllegalArgumentException> { checkedPrimitive(it) }
                }
            }
    }
}
