package it.unibo.tuprolog.solve.primitive

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.solve.primitive.testutils.PrimitiveWrapperUtils.createPrimitiveRequest
import it.unibo.tuprolog.solve.primitive.testutils.PrimitiveWrapperUtils.defaultPrimitiveResult
import it.unibo.tuprolog.solve.primitive.testutils.PrimitiveWrapperUtils.primitive
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.notMatchingCases
import it.unibo.tuprolog.solve.primitive.testutils.WrapperUtils.withMatchingCases

/**
 * Test class for [Primitive] companion functions
 *
 * @author Enrico
 */
class PrimitiveSpec :
    FunSpec({
        withMatchingCases("enforcing-signature/accepts") { case ->
            val checkedPrimitive = Primitive.enforcingSignature(case.signature, primitive)
            checkedPrimitive.solve(case.request(::createPrimitiveRequest)) shouldBeSameInstanceAs defaultPrimitiveResult
        }

        withTests(nameFn = { "enforcing-signature/rejects/${it.id}" }, notMatchingCases) { case ->
            val checkedPrimitive = Primitive.enforcingSignature(case.signature, primitive)
            shouldThrow<IllegalArgumentException> { checkedPrimitive.solve(case.request(::createPrimitiveRequest)) }
        }
    })
