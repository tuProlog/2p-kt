package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.solve.primitive.testutils.PrimitiveWrapperUtils.createPrimitiveRequest
import it.unibo.tuprolog.solve.primitive.testutils.PrimitiveWrapperUtils.defaultPrimitiveResult
import it.unibo.tuprolog.solve.primitive.testutils.PrimitiveWrapperUtils.primitive
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.allSignatures
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.wrapperToMatchingSignatureRequest
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.wrapperToNotMatchingSignatureRequest
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
        wrapperToMatchingSignatureRequest(Primitive.Companion::enforcingSignature, primitive, ::createPrimitiveRequest)
            .zip(allSignatures)
            .forEach { (primitiveToGoodRequests, primitiveSignature) ->
                val (checkedPrimitive, goodRequests) = primitiveToGoodRequests
                goodRequests.forEach {
                    if (primitiveSignature.vararg) return
                    assertEquals(defaultPrimitiveResult, checkedPrimitive.solve(it))
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
        wrapperToNotMatchingSignatureRequest(
            Primitive.Companion::enforcingSignature,
            primitive,
            ::createPrimitiveRequest,
        ).forEach { (checkedPrimitive, badRequests) ->
            badRequests.forEach {
                assertFailsWith<IllegalArgumentException> { checkedPrimitive.solve(it) }
            }
        }
    }
}
