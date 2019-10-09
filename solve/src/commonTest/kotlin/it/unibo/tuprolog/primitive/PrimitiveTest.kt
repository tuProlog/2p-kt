package it.unibo.tuprolog.primitive

import it.unibo.tuprolog.primitive.testutils.PrimitiveUtils
import it.unibo.tuprolog.primitive.testutils.PrimitiveUtils.primitiveToBadRequests
import it.unibo.tuprolog.primitive.testutils.PrimitiveUtils.primitiveToGoodRequests
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
        primitiveToGoodRequests(::primitiveOf).zip(PrimitiveUtils.primitiveSignatures).forEach { (testData, support) ->
            val (checkedPrimitive, goodRequests) = testData
            goodRequests.forEach {
                if (support.vararg) return
                assertEquals(emptySequence(), checkedPrimitive(it))
            }
        }
        // TODO delete above test and enable the code below after solving TODO in "Signature"
//        primitiveToGoodRequests(::primitiveOf).forEach { (checkedPrimitive, goodRequests) ->
//            goodRequests.forEach {
//                assertEquals(emptySequence(), checkedPrimitive(it))
//            }
//        }
    }

    @Test
    fun primitiveOfComplainsIfDifferentRequestSignatureIsDetected() {
        primitiveToBadRequests(::primitiveOf).forEach { (checkedPrimitive, badRequests) ->
            badRequests.forEach {
                assertFailsWith<IllegalArgumentException> { checkedPrimitive(it) }
            }
        }
    }
}
