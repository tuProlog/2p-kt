package it.unibo.tuprolog.solve.primitive

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.assertOverFailure
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringAllArgumentsAreInstantiated
import it.unibo.tuprolog.solve.primitive.testutils.PrimitiveWrapperUtils.allGroundRequests
import it.unibo.tuprolog.solve.primitive.testutils.PrimitiveWrapperUtils.createPrimitiveRequest
import it.unibo.tuprolog.solve.primitive.testutils.PrimitiveWrapperUtils.createPrimitiveWrapper
import it.unibo.tuprolog.solve.primitive.testutils.PrimitiveWrapperUtils.defaultPrimitiveResult
import it.unibo.tuprolog.solve.primitive.testutils.PrimitiveWrapperUtils.nonAllGroundRequests
import it.unibo.tuprolog.solve.primitive.testutils.PrimitiveWrapperUtils.primitive
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.wrapperToMatchingSignatureRequest
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.wrapperToNotMatchingSignatureRequest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

/**
 * Test class for [PrimitiveWrapper]
 *
 * @author Enrico
 */
class PrimitiveWrapperSpec :
    FunSpec({
        test("primitive-works-if-correct-request-provided") {
            wrapperToMatchingSignatureRequest(::createPrimitiveWrapper, primitive, ::createPrimitiveRequest)
                .forEach { (wrapper, acceptedRequests) ->
                    acceptedRequests.forEach {
                        // TODO remove this "if" after solving TODO in "Signature"
                        if (wrapper.signature.vararg) return@test
                        assertEquals(defaultPrimitiveResult, wrapper.implementation.solve(it))
                    }
                }
        }

        test("primitive-complains-with-wrong-request-signature-or-arguments") {
            wrapperToNotMatchingSignatureRequest(::createPrimitiveWrapper, primitive, ::createPrimitiveRequest)
                .forEach { (wrapper, badRequests) ->
                    badRequests.forEach {
                        assertFailsWith<IllegalArgumentException> { wrapper.implementation.solve(it) }
                    }
                }
        }

        test("ensuring-all-argument-instantiated-returns-same-request-if-all-arguments-instantiated") {
            allGroundRequests.forEach {
                assertSame(it, it.ensuringAllArgumentsAreInstantiated())
            }
        }

        test("ensuring-all-argument-instantiated-throw-instantiation-error-if-some-argument-is-not-instantiated") {
            nonAllGroundRequests.forEach {
                assertFailsWith<InstantiationError> { it.ensuringAllArgumentsAreInstantiated() }
            }
        }

        test("ensuring-all-argument-instantiated-instantiation-error-contains-correct-data") {
            nonAllGroundRequests.forEach { request ->
                assertOverFailure<InstantiationError>(
                    { request.ensuringAllArgumentsAreInstantiated() },
                ) { error ->
                    assertSame(request.context, error.context)
                    //                assertSame(request.arguments.first { !it.isGround }, error.extraData)
                }
            }
        }
    })
