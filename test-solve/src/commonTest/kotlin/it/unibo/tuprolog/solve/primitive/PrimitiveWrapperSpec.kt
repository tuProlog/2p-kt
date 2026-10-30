package it.unibo.tuprolog.solve.primitive

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.assertOverFailure
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringAllArgumentsAreInstantiated
import it.unibo.tuprolog.solve.primitive.testutils.PrimitiveWrapperUtils.createPrimitiveRequest
import it.unibo.tuprolog.solve.primitive.testutils.PrimitiveWrapperUtils.createPrimitiveWrapper
import it.unibo.tuprolog.solve.primitive.testutils.PrimitiveWrapperUtils.defaultPrimitiveResult
import it.unibo.tuprolog.solve.primitive.testutils.PrimitiveWrapperUtils.primitive
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.allCases
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.notMatchingCases
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.withMatchingCases
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
        val groundCases = allCases.filter { it.struct.isGround }

        val nonGroundCases = allCases.filterNot { it.struct.isGround }

        withMatchingCases("accepts") { case ->
            val wrapper = createPrimitiveWrapper(case.signature, primitive)
            assertEquals(defaultPrimitiveResult, wrapper.implementation.solve(case.request(::createPrimitiveRequest)))
        }

        withTests(nameFn = { "rejects/${it.id}" }, notMatchingCases) { case ->
            val wrapper = createPrimitiveWrapper(case.signature, primitive)
            assertFailsWith<IllegalArgumentException> {
                wrapper.implementation.solve(case.request(::createPrimitiveRequest))
            }
        }

        withTests(
            nameFn = { "ensuring-all-arguments-instantiated/returns-same-request/${it.id}" },
            groundCases,
        ) { case ->
            val request = case.request(::createPrimitiveRequest)
            assertSame(request, request.ensuringAllArgumentsAreInstantiated())
        }

        withTests(nameFn = { "ensuring-all-arguments-instantiated/throws/${it.id}" }, nonGroundCases) { case ->
            val request = case.request(::createPrimitiveRequest)
            assertFailsWith<InstantiationError> { request.ensuringAllArgumentsAreInstantiated() }
        }

        withTests(nameFn = { "ensuring-all-arguments-instantiated/error-data/${it.id}" }, nonGroundCases) { case ->
            val request = case.request(::createPrimitiveRequest)
            assertOverFailure<InstantiationError>(
                { request.ensuringAllArgumentsAreInstantiated() },
            ) { error ->
                assertSame(request.context, error.context)
            }
        }
    })
