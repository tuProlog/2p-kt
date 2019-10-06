package it.unibo.tuprolog.primitive

import it.unibo.tuprolog.primitive.PrimitiveWrapper.Companion.ensuringAllArgumentsAreInstantiated
import it.unibo.tuprolog.primitive.PrimitiveWrapper.Companion.ensuringAllArgumentsAreNumeric
import it.unibo.tuprolog.primitive.testutils.PrimitiveWrapperUtils.allGroundRequests
import it.unibo.tuprolog.primitive.testutils.PrimitiveWrapperUtils.allNumericArgsRequests
import it.unibo.tuprolog.primitive.testutils.PrimitiveWrapperUtils.assertOnError
import it.unibo.tuprolog.primitive.testutils.PrimitiveWrapperUtils.createRequest
import it.unibo.tuprolog.primitive.testutils.PrimitiveWrapperUtils.nonAllGroundRequests
import it.unibo.tuprolog.primitive.testutils.PrimitiveWrapperUtils.notAllNumericArgsRequest
import it.unibo.tuprolog.primitive.testutils.PrimitiveWrapperUtils.primitiveWrappersToSignatureMap
import it.unibo.tuprolog.primitive.testutils.PrimitiveWrapperUtils.primitivesToBadRequests
import it.unibo.tuprolog.primitive.testutils.PrimitiveWrapperUtils.primitivesToCorrectRequests
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
        primitiveWrappersToSignatureMap.forEach { (signature, wrapper) ->
            assertEquals(signature, wrapper.signature)
        }
    }

    @Test
    fun functorCorrect() {
        primitiveWrappersToSignatureMap.forEach { (signature, wrapper) ->
            assertEquals(signature.name, wrapper.functor)
        }
    }

    @Test
    fun primitiveWorksIfCorrectRequestProvided() {
        primitivesToCorrectRequests.forEach { (wrapper, acceptedRequests) ->
            acceptedRequests.forEach {
                if (wrapper.signature.vararg) return // TODO remove this "if" after solving TODO in "Signature"
                assertEquals(emptySequence(), wrapper.primitive(createRequest(it.extractSignature(), it.argsList)))
            }
        }
    }

    @Test
    fun primitiveComplainsWithWrongRequestSignatureOrArguments() {
        primitivesToBadRequests.forEach { (wrapper, badRequests) ->
            badRequests.forEach {
                assertFailsWith<IllegalArgumentException> { wrapper.primitive(createRequest(it.extractSignature(), it.argsList)) }
            }
        }
    }

    @Test
    fun descriptionPairCorrect() {
        primitiveWrappersToSignatureMap.forEach { (signature, wrapper) ->
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
