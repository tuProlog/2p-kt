package it.unibo.tuprolog.solve.primitive

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.primitive.testutils.PrimitiveWrapperUtils.createPrimitiveRequest
import it.unibo.tuprolog.solve.primitive.testutils.PrimitiveWrapperUtils.defaultPrimitiveResult
import it.unibo.tuprolog.solve.primitive.testutils.PrimitiveWrapperUtils.primitive
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.notMatchingCases
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.withMatchingCases
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [Primitive] companion functions
 *
 * @author Enrico
 */
class PrimitiveSpec :
    FunSpec({
        withMatchingCases("enforcing-signature/accepts") { case ->
            val checkedPrimitive = Primitive.enforcingSignature(case.signature, primitive)
            assertEquals(defaultPrimitiveResult, checkedPrimitive.solve(case.request(::createPrimitiveRequest)))
        }

        withTests(nameFn = { "enforcing-signature/rejects/${it.id}" }, notMatchingCases) { case ->
            val checkedPrimitive = Primitive.enforcingSignature(case.signature, primitive)
            assertFailsWith<IllegalArgumentException> { checkedPrimitive.solve(case.request(::createPrimitiveRequest)) }
        }
    })
