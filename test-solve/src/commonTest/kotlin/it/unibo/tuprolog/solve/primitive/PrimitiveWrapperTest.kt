package it.unibo.tuprolog.solve.primitive

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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

/**
 * Test class for [PrimitiveWrapper]
 *
 * @author Enrico
 */
internal class PrimitiveWrapperTest {
    @Test
    fun primitiveWorksIfCorrectRequestProvided() {
        wrapperToMatchingSignatureRequest(::createPrimitiveWrapper, primitive, ::createPrimitiveRequest)
            .forEach { (wrapper, acceptedRequests) ->
                acceptedRequests.forEach {
                    if (wrapper.signature.vararg) return // TODO remove this "if" after solving TODO in "Signature"
                    assertEquals(defaultPrimitiveResult, wrapper.implementation.solve(it))
                }
            }
    }

    @Test
    fun primitiveComplainsWithWrongRequestSignatureOrArguments() {
        wrapperToNotMatchingSignatureRequest(::createPrimitiveWrapper, primitive, ::createPrimitiveRequest)
            .forEach { (wrapper, badRequests) ->
                badRequests.forEach {
                    assertFailsWith<IllegalArgumentException> { wrapper.implementation.solve(it) }
                }
            }
    }

    @Test
    fun ensuringAllArgumentInstantiatedReturnsSameRequestIfAllArgumentsInstantiated() {
        allGroundRequests.forEach {
            assertSame(it, it.ensuringAllArgumentsAreInstantiated())
        }
    }

    @Test
    fun ensuringAllArgumentInstantiatedThrowInstantiationErrorIfSomeArgumentIsNotInstantiated() {
        nonAllGroundRequests.forEach {
            assertFailsWith<InstantiationError> { it.ensuringAllArgumentsAreInstantiated() }
        }
    }

    @Test
    fun ensuringAllArgumentInstantiatedInstantiationErrorContainsCorrectData() {
        nonAllGroundRequests.forEach { request ->
            assertOverFailure<InstantiationError>({ request.ensuringAllArgumentsAreInstantiated() }) { error ->
                assertSame(request.context, error.context)
//                assertSame(request.arguments.first { !it.isGround }, error.extraData)
            }
        }
    }
}
