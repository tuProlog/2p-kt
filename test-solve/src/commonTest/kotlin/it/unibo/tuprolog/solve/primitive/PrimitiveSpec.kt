package it.unibo.tuprolog.solve.primitive

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.primitive.testutils.PrimitiveWrapperUtils.createPrimitiveRequest
import it.unibo.tuprolog.solve.primitive.testutils.PrimitiveWrapperUtils.defaultPrimitiveResult
import it.unibo.tuprolog.solve.primitive.testutils.PrimitiveWrapperUtils.primitive
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.allSignatures
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.wrapperToMatchingSignatureRequest
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.wrapperToNotMatchingSignatureRequest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [Primitive] companion functions
 *
 * @author Enrico
 */
class PrimitiveSpec :
    FunSpec({
        test("primitive-of-returns-primitive-behaving-exactly-as-provided-one") {
            wrapperToMatchingSignatureRequest(
                Primitive.Companion::enforcingSignature,
                primitive,
                ::createPrimitiveRequest,
            ).zip(allSignatures)
                .forEach { (primitiveToGoodRequests, primitiveSignature) ->
                    val (checkedPrimitive, goodRequests) = primitiveToGoodRequests
                    goodRequests.forEach {
                        if (primitiveSignature.vararg) return@test
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

        test("primitive-of-complains-if-different-request-signature-is-detected") {
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
    })
