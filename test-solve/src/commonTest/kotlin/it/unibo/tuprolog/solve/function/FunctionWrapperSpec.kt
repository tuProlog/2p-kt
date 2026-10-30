package it.unibo.tuprolog.solve.function

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.function.testutils.FunctionWrapperUtils.createFunctionRequest
import it.unibo.tuprolog.solve.function.testutils.FunctionWrapperUtils.createFunctionWrapper
import it.unibo.tuprolog.solve.function.testutils.FunctionWrapperUtils.defaultFunctionResult
import it.unibo.tuprolog.solve.function.testutils.FunctionWrapperUtils.function
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.wrapperToMatchingSignatureRequest
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.wrapperToNotMatchingSignatureRequest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [FunctionWrapper]
 *
 * @author Enrico
 */
class FunctionWrapperSpec :
    FunSpec({
        test("function-works-if-correct-request-provided") {
            wrapperToMatchingSignatureRequest(::createFunctionWrapper, function, ::createFunctionRequest)
                .forEach { (wrapper, acceptedRequests) ->
                    acceptedRequests.forEach {
                        // TODO remove this "if" after solving TODO in "Signature"
                        if (wrapper.signature.vararg) return@test
                        assertEquals(defaultFunctionResult, wrapper.implementation.compute(it))
                    }
                }
        }

        test("function-complains-with-wrong-request-signature-or-arguments") {
            wrapperToNotMatchingSignatureRequest(::createFunctionWrapper, function, ::createFunctionRequest)
                .forEach { (wrapper, badRequests) ->
                    badRequests.forEach {
                        assertFailsWith<IllegalArgumentException> { wrapper.implementation.compute(it) }
                    }
                }
        }
    })
