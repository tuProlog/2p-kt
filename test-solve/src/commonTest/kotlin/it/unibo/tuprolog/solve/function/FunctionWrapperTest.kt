package it.unibo.tuprolog.solve.function

import it.unibo.tuprolog.solve.function.testutils.FunctionWrapperUtils.createFunctionRequest
import it.unibo.tuprolog.solve.function.testutils.FunctionWrapperUtils.createFunctionWrapper
import it.unibo.tuprolog.solve.function.testutils.FunctionWrapperUtils.defaultFunctionResult
import it.unibo.tuprolog.solve.function.testutils.FunctionWrapperUtils.function
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.wrapperToMatchingSignatureRequest
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.wrapperToNotMatchingSignatureRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [FunctionWrapper]
 *
 * @author Enrico
 */
internal class FunctionWrapperTest {

    @Test
    fun functionWorksIfCorrectRequestProvided() {
        wrapperToMatchingSignatureRequest(::createFunctionWrapper, function, ::createFunctionRequest)
            .forEach { (wrapper, acceptedRequests) ->
                acceptedRequests.forEach {
                    if (wrapper.signature.vararg) return // TODO remove this "if" after solving TODO in "Signature"
                    assertEquals(defaultFunctionResult, wrapper.implementation.compute(it))
                }
            }
    }

    @Test
    fun functionComplainsWithWrongRequestSignatureOrArguments() {
        wrapperToNotMatchingSignatureRequest(::createFunctionWrapper, function, ::createFunctionRequest)
            .forEach { (wrapper, badRequests) ->
                badRequests.forEach {
                    assertFailsWith<IllegalArgumentException> { wrapper.implementation.compute(it) }
                }
            }
    }
}
