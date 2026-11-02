package it.unibo.tuprolog.solve.function

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.solve.function.testutils.FunctionWrapperUtils.createFunctionRequest
import it.unibo.tuprolog.solve.function.testutils.FunctionWrapperUtils.createFunctionWrapper
import it.unibo.tuprolog.solve.function.testutils.FunctionWrapperUtils.defaultFunctionResult
import it.unibo.tuprolog.solve.function.testutils.FunctionWrapperUtils.function
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.notMatchingCases
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.withMatchingCases

/**
 * Test class for [FunctionWrapper]
 *
 * @author Enrico
 */
class FunctionWrapperSpec :
    FunSpec({
        withMatchingCases("accepts") { case ->
            val wrapper = createFunctionWrapper(case.signature, function)
            wrapper.implementation.compute(case.request(::createFunctionRequest)) shouldBe defaultFunctionResult
        }

        withTests(nameFn = { "rejects/${it.id}" }, notMatchingCases) { case ->
            val wrapper = createFunctionWrapper(case.signature, function)
            shouldThrow<IllegalArgumentException> {
                wrapper.implementation.compute(case.request(::createFunctionRequest))
            }
        }
    })
