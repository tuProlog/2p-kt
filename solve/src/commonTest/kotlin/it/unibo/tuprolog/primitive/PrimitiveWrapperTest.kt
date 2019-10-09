package it.unibo.tuprolog.primitive

import it.unibo.tuprolog.primitive.PrimitiveWrapper.Companion.ensuringAllArgumentsAreInstantiated
import it.unibo.tuprolog.primitive.PrimitiveWrapper.Companion.ensuringAllArgumentsAreNumeric
import it.unibo.tuprolog.primitive.testutils.PrimitiveUtils.primitiveToBadRequests
import it.unibo.tuprolog.primitive.testutils.PrimitiveUtils.primitiveToGoodRequests
import it.unibo.tuprolog.primitive.testutils.PrimitiveWrapperUtils.allGroundRequests
import it.unibo.tuprolog.primitive.testutils.PrimitiveWrapperUtils.allNumericArgsRequests
import it.unibo.tuprolog.primitive.testutils.PrimitiveWrapperUtils.assertOnError
import it.unibo.tuprolog.primitive.testutils.PrimitiveWrapperUtils.createPrimitiveWrapper
import it.unibo.tuprolog.primitive.testutils.PrimitiveWrapperUtils.nonAllGroundRequests
import it.unibo.tuprolog.primitive.testutils.PrimitiveWrapperUtils.notAllNumericArgsRequest
import it.unibo.tuprolog.primitive.testutils.PrimitiveWrapperUtils.primitiveWrappersToSignatures
import it.unibo.tuprolog.solve.exception.prologerror.InstantiationError
import it.unibo.tuprolog.solve.exception.prologerror.TypeError
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
    fun signatureCorrect() {
        primitiveWrappersToSignatures.forEach { (wrapper, signature) ->
            assertEquals(signature, wrapper.signature)
        }
    }

    @Test
    fun functorCorrect() {
        primitiveWrappersToSignatures.forEach { (wrapper, signature) ->
            assertEquals(signature.name, wrapper.functor)
        }
    }

    @Test
    fun primitiveWorksIfCorrectRequestProvided() {
        primitiveToGoodRequests(::createPrimitiveWrapper).forEach { (wrapper, acceptedRequests) ->
            acceptedRequests.forEach {
                if (wrapper.signature.vararg) return // TODO remove this "if" after solving TODO in "Signature"
                assertEquals(emptySequence(), wrapper.primitive(it))
            }
        }
    }

    @Test
    fun primitiveComplainsWithWrongRequestSignatureOrArguments() {
        primitiveToBadRequests(::createPrimitiveWrapper).forEach { (wrapper, badRequests) ->
            badRequests.forEach {
                assertFailsWith<IllegalArgumentException> { wrapper.primitive(it) }
            }
        }
    }

    @Test
    fun descriptionPairCorrect() {
        primitiveWrappersToSignatures.forEach { (wrapper, signature) ->
            assertEquals(with(wrapper) { signature to primitive }, wrapper.descriptionPair)
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
            assertOnError<InstantiationError>({ request.ensuringAllArgumentsAreInstantiated() }) { error ->
                assertSame(request.context, error.context)
                assertSame(request.arguments.first { !it.isGround }, error.extraData)
            }
        }
    }

    @Test
    fun ensuringAllArgumentAreNumericReturnsSameRequestIfAllArgumentsAreNumeric() {
        allNumericArgsRequests.forEach {
            assertSame(it, it.ensuringAllArgumentsAreNumeric())
        }
    }

    @Test
    fun ensuringAllArgumentAreNumericThrowTypeErrorIfSomeArgumentIsNotNumeric() {
        notAllNumericArgsRequest.forEach {
            assertFailsWith<TypeError> { it.ensuringAllArgumentsAreNumeric() }
        }
    }

    @Test
    fun ensuringAllArgumentAreNumericTypeErrorContainsCorrectData() {
        notAllNumericArgsRequest.forEach { request ->
            assertOnError<TypeError>({ request.ensuringAllArgumentsAreNumeric() }) { error ->
                assertSame(request.context, error.context)
                with(request.arguments.first { !it.isNumber }) {
                    assertSame(this, error.actualValue)
                    assertSame(this, error.extraData)
                }
                assertSame(TypeError.Expected.NUMBER, error.expectedType)
            }
        }
    }
}
