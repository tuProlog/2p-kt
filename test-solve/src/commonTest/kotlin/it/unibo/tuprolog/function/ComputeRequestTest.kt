package it.unibo.tuprolog.function

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.function.testutils.ComputeUtils.aRequestIssuingInstant
import it.unibo.tuprolog.function.testutils.ComputeUtils.aResult
import it.unibo.tuprolog.function.testutils.ComputeUtils.aSignature
import it.unibo.tuprolog.function.testutils.ComputeUtils.aVarargSignature
import it.unibo.tuprolog.function.testutils.ComputeUtils.anArgumentList
import it.unibo.tuprolog.function.testutils.ComputeUtils.anExecutionContext
import it.unibo.tuprolog.function.testutils.ComputeUtils.anExecutionMaxDuration
import it.unibo.tuprolog.function.testutils.ComputeUtils.assertContainsCorrectData
import it.unibo.tuprolog.function.testutils.ComputeUtils.createRequest
import it.unibo.tuprolog.function.testutils.ComputeUtils.defaultResponse
import it.unibo.tuprolog.function.testutils.ComputeUtils.varargArgumentList
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.currentTimeInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [Compute.Request]
 *
 * @author Enrico
 */
internal class ComputeRequestTest {

    @Test
    fun requestInsertedDataCorrect() {
        val toBeTested = Compute.Request(
            aSignature,
            anArgumentList,
            anExecutionContext,
            aRequestIssuingInstant,
            anExecutionMaxDuration
        )

        toBeTested.assertContainsCorrectData(
            aSignature,
            anArgumentList,
            anExecutionContext,
            aRequestIssuingInstant,
            anExecutionMaxDuration
        )
    }

    @Test
    fun requestDefaultValuesCorrect() {
        val toBeTested = Compute.Request(aSignature, anArgumentList, anExecutionContext)

        toBeTested.assertContainsCorrectData(
            aSignature,
            anArgumentList,
            anExecutionContext,
            currentTimeInstant(),
            TimeDuration.MAX_VALUE
        )
    }

    @Test
    fun requestConstructorComplainsWithWrongArityAndArgumentsCount() {
        assertFailsWith<IllegalArgumentException> { createRequest(arguments = emptyList()) }
        assertFailsWith<IllegalArgumentException> { createRequest(arguments = anArgumentList + Truth.ofFalse()) }
    }

    @Test
    fun requestConstructorComplainsWithLessThanArityArgumentsIfVarargSignature() {
        assertFailsWith<IllegalArgumentException> { createRequest(aVarargSignature, emptyList()) }
    }

    @Test
    fun requestConstructorPermitsMoreThanArityArgumentsIfSignatureVararg() {
        createRequest(aVarargSignature, anArgumentList + Truth.ofFalse())
    }

    @Test
    fun requestConstructorComplainsWithNegativeMaxDuration() {
        createRequest(executionMaxDuration = 0)
        assertFailsWith<IllegalArgumentException> { createRequest(executionMaxDuration = -1) }
    }

    @Test
    fun requestConstructorComplainsWithNegativeRequestIssuingInstant() {
        createRequest(requestIssuingInstant = 0)
        assertFailsWith<IllegalArgumentException> { createRequest(requestIssuingInstant = -1) }
    }

    @Test
    fun requestComputesCorrectlyQueryStruct() {
        assertEquals(Struct.of(aSignature.name, anArgumentList), createRequest(aSignature, anArgumentList).query)
        assertEquals(
            Struct.of(aVarargSignature.name, varargArgumentList),
            createRequest(aVarargSignature, varargArgumentList).query
        )
    }

    @Test
    fun equalsWorksAsExpected() {
        val aRequest = createRequest()
        assertEquals(aRequest.copy(), aRequest)
    }

    @Test
    fun replyWithCreatesCorrectResponse() {
        assertEquals(createRequest().replyWith(aResult), defaultResponse)
    }
}
