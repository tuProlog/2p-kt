package it.unibo.tuprolog.primitive

import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.testutils.DummyInstances
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [Primitive] companion functions
 *
 * @author Enrico
 */
internal class PrimitiveTest {

    private val signature = Signature("f", 1)

    private val aRequest = Solve.Request(signature, listOf(Truth.`true`()), DummyInstances.executionContext)
    private val aResponse = Solve.Response(Solution.No(Truth.fail()), DummyInstances.executionContext)

    private val testPrimitive: Primitive = { _ -> sequenceOf(aResponse) }

    @Test
    fun checkedPrimitiveForReturnsPrimitiveBehavingExactlyAsProvidedOne() {
        val toBeTested: Primitive = checkedPrimitiveFor(signature, testPrimitive)

        assertEquals(testPrimitive(aRequest).toList(), toBeTested(aRequest).toList())
    }

    @Test
    fun checkedPrimitiveComplainsIfDifferentRequestSignatureIsDetected() {
        assertFailsWith<IllegalArgumentException> { checkedPrimitiveFor(signature.copy(name = "other"), testPrimitive)(aRequest) }
        assertFailsWith<IllegalArgumentException> { checkedPrimitiveFor(signature.copy(arity = 0), testPrimitive)(aRequest) }
    }
}
