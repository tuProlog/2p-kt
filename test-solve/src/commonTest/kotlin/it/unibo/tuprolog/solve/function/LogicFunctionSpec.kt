package it.unibo.tuprolog.solve.function

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.function.testutils.FunctionWrapperUtils.createFunctionRequest
import it.unibo.tuprolog.solve.function.testutils.FunctionWrapperUtils.defaultFunctionResult
import it.unibo.tuprolog.solve.function.testutils.FunctionWrapperUtils.function
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.allSignatures
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.wrapperToMatchingSignatureRequest
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.wrapperToNotMatchingSignatureRequest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [LogicFunction]
 *
 * @author Enrico
 */
class LogicFunctionSpec :
    FunSpec({
        test("function-of-returns-prolog-function-behaving-exactly-as-provided-one") {
            wrapperToMatchingSignatureRequest(
                LogicFunction.Companion::enforcingSignature,
                function,
                ::createFunctionRequest,
            ).zip(allSignatures)
                .forEach { (functionToGoodRequests, functionSignature) ->
                    val (checkedFunction, goodRequests) = functionToGoodRequests
                    goodRequests.forEach {
                        if (functionSignature.vararg) return@test
                        assertEquals(defaultFunctionResult, checkedFunction.compute(it))
                    }
                }
            // TODO delete above test and enable the code below after solving TODO in "Signature"
            //        wrapperToMatchingSignatureRequest(::functionOf, function, ::createRequest).forEach { (checkedFunction, goodRequests) ->
            //            goodRequests.forEach {
            //                assertEquals(defaultFunctionResult, checkedFunction(it))
            //            }
            //        }
        }

        test("function-of-complains-if-different-request-signature-is-detected") {
            wrapperToNotMatchingSignatureRequest(
                LogicFunction.Companion::enforcingSignature,
                function,
                ::createFunctionRequest,
            ).forEach { (checkedFunction, badRequests) ->
                badRequests.forEach {
                    assertFailsWith<IllegalArgumentException> { checkedFunction.compute(it) }
                }
            }
        }
    })
