package it.unibo.tuprolog.solve.function

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.function.testutils.FunctionWrapperUtils.createFunctionRequest
import it.unibo.tuprolog.solve.function.testutils.FunctionWrapperUtils.createFunctionWrapper
import it.unibo.tuprolog.solve.function.testutils.FunctionWrapperUtils.defaultFunctionResult
import it.unibo.tuprolog.solve.function.testutils.FunctionWrapperUtils.function
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.notMatchingCases
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.withMatchingCases
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [FunctionWrapper]
 *
 * @author Enrico
 */
class FunctionWrapperSpec :
    FunSpec({
        withMatchingCases("accepts") { case ->
            val wrapper = createFunctionWrapper(case.signature, function)
            assertEquals(defaultFunctionResult, wrapper.implementation.compute(case.request(::createFunctionRequest)))
        }

        withTests(nameFn = { "rejects/${it.id}" }, notMatchingCases) { case ->
            val wrapper = createFunctionWrapper(case.signature, function)
            assertFailsWith<IllegalArgumentException> {
                wrapper.implementation.compute(case.request(::createFunctionRequest))
            }
        }
    })
