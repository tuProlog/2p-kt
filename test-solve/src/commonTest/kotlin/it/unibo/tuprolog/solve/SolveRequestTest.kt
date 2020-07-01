package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.testutils.SolveUtils.aRequestIssuingInstant
import it.unibo.tuprolog.solve.testutils.SolveUtils.aSideEffectManager
import it.unibo.tuprolog.solve.testutils.SolveUtils.aSignature
import it.unibo.tuprolog.solve.testutils.SolveUtils.aVarargSignature
import it.unibo.tuprolog.solve.testutils.SolveUtils.anArgumentList
import it.unibo.tuprolog.solve.testutils.SolveUtils.anExecutionContext
import it.unibo.tuprolog.solve.testutils.SolveUtils.anExecutionMaxDuration
import it.unibo.tuprolog.solve.testutils.SolveUtils.createRequest
import it.unibo.tuprolog.solve.testutils.SolveUtils.defaultRequestFailedResponse
import it.unibo.tuprolog.solve.testutils.SolveUtils.defaultRequestHaltedResponse
import it.unibo.tuprolog.solve.testutils.SolveUtils.defaultRequestResponses
import it.unibo.tuprolog.solve.testutils.SolveUtils.defaultRequestSuccessResponse
import it.unibo.tuprolog.solve.testutils.SolveUtils.differentDynamicKB
import it.unibo.tuprolog.solve.testutils.SolveUtils.differentFlags
import it.unibo.tuprolog.solve.testutils.SolveUtils.differentLibraries
import it.unibo.tuprolog.solve.testutils.SolveUtils.differentStaticKB
import it.unibo.tuprolog.solve.testutils.SolveUtils.solutionException
import it.unibo.tuprolog.solve.testutils.SolveUtils.solutionSubstitution
import it.unibo.tuprolog.solve.testutils.SolveUtils.varargArgumentList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Test class for [Solve.Request]
 *
 * @author Enrico
 */
internal class SolveRequestTest {

    companion object {
        private const val TOLERANCE = 10L
    }

    @Test
    fun requestInsertedDataCorrect() {
        val toBeTested = Solve.Request(
            aSignature,
            anArgumentList,
            anExecutionContext,
            aRequestIssuingInstant,
            anExecutionMaxDuration
        )

        assertEquals(aSignature, toBeTested.signature)
        assertEquals(anArgumentList, toBeTested.arguments)
        assertEquals(anExecutionContext, toBeTested.context)
        assertEquals(aRequestIssuingInstant, toBeTested.requestIssuingInstant)
        assertEquals(anExecutionMaxDuration, toBeTested.executionMaxDuration)
    }

    @Test
    fun requestDefaultValuesCorrect() {
        val toBeTested = Solve.Request(aSignature, anArgumentList, anExecutionContext)

        assertTrue { currentTimeInstant() - toBeTested.requestIssuingInstant < TOLERANCE }
        assertEquals(TimeDuration.MAX_VALUE, toBeTested.executionMaxDuration)
    }

    @Test
    fun requestConstructorComplainsWithWrongArityAndArgumentsCount() {
        assertFailsWith<IllegalArgumentException> { createRequest(arguments = emptyList()) }
        assertFailsWith<IllegalArgumentException> { createRequest(arguments = anArgumentList + Truth.FAIL) }
    }

    @Test
    fun requestConstructorComplainsWithLessThanArityArgumentsIfVarargSignature() {
        assertFailsWith<IllegalArgumentException> { createRequest(aVarargSignature, emptyList()) }
    }

    @Test
    fun requestConstructorPermitsMoreThanArityArgumentsIfSignatureVararg() {
        createRequest(aVarargSignature, anArgumentList + Truth.FAIL)
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
    fun replyWithSolutionCreatesCorrectResponse() {
        defaultRequestResponses.forEach {
            assertEquals(
                createRequest().replyWith(
                    it.solution,
                    aSideEffectManager
                ) {
                    resetLibraries(differentLibraries)
                    resetFlags(differentFlags)
                    resetStaticKb(differentStaticKB)
                    resetDynamicKb(differentDynamicKB)
                },
                it
            )
        }
    }

    @Test
    fun replySuccessCreatesCorrectResponse() {
        assertEquals(
            createRequest().replySuccess(
                solutionSubstitution,
                aSideEffectManager
            ) {
                resetLibraries(differentLibraries)
                resetFlags(differentFlags)
                resetStaticKb(differentStaticKB)
                resetDynamicKb(differentDynamicKB)
            },
            defaultRequestSuccessResponse
        )
    }

    @Test
    fun replyFailCreatesCorrectResponse() {
        assertEquals(
            createRequest().replyFail(
                aSideEffectManager
            ) {
                resetLibraries(differentLibraries)
                resetFlags(differentFlags)
                resetStaticKb(differentStaticKB)
                resetDynamicKb(differentDynamicKB)
            },
            defaultRequestFailedResponse
        )
    }

    @Test
    fun replyExceptionCreatesCorrectResponse() {
        assertEquals(
            createRequest().replyException(
                solutionException,
                aSideEffectManager
            ) {
                resetLibraries(differentLibraries)
                resetFlags(differentFlags)
                resetStaticKb(differentStaticKB)
                resetDynamicKb(differentDynamicKB)
            },
            defaultRequestHaltedResponse
        )
    }

    @Test
    fun replyWithBooleanCreatesCorrectResponse() {
        assertEquals(
            createRequest().replyWith(
                true,
                aSideEffectManager
            ) {
                resetLibraries(differentLibraries)
                resetFlags(differentFlags)
                resetStaticKb(differentStaticKB)
                resetDynamicKb(differentDynamicKB)
            },
            defaultRequestSuccessResponse.copy(
                solution = (defaultRequestSuccessResponse.solution as Solution.Yes).copy(
                    substitution = Substitution.empty()
                )
            )
        )

        assertEquals(
            createRequest().replyWith(
                false,
                aSideEffectManager
            ) {
                resetLibraries(differentLibraries)
                resetFlags(differentFlags)
                resetStaticKb(differentStaticKB)
                resetDynamicKb(differentDynamicKB)
            },
            defaultRequestFailedResponse
        )
    }
}
