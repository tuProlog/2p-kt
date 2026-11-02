package it.unibo.tuprolog.solve.function

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.solve.function.testutils.FunctionWrapperUtils.createFunctionRequest
import it.unibo.tuprolog.solve.function.testutils.FunctionWrapperUtils.defaultFunctionResult
import it.unibo.tuprolog.solve.function.testutils.FunctionWrapperUtils.function
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.notMatchingCases
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.withMatchingCases

/**
 * Test class for [LogicFunction]
 *
 * @author Enrico
 */
class LogicFunctionSpec :
    FunSpec({
        withMatchingCases("enforcing-signature/accepts") { case ->
            val checkedFunction = LogicFunction.enforcingSignature(case.signature, function)
            checkedFunction.compute(case.request(::createFunctionRequest)) shouldBe defaultFunctionResult
        }

        withTests(nameFn = { "enforcing-signature/rejects/${it.id}" }, notMatchingCases) { case ->
            val checkedFunction = LogicFunction.enforcingSignature(case.signature, function)
            shouldThrow<IllegalArgumentException> { checkedFunction.compute(case.request(::createFunctionRequest)) }
        }
    })
