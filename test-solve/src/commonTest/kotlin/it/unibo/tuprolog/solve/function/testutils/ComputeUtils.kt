package it.unibo.tuprolog.solve.function.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.TimeInstant
import it.unibo.tuprolog.solve.function.Compute
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Utils singleton to help testing [Compute]
 *
 * @author Enrico
 */
internal object ComputeUtils {

    // Request parameters
    internal val aSignature = Signature("ciao", 2)
    internal val anArgumentList = listOf(Atom.of("a"), Truth.TRUE)
    internal val anExecutionContext = DummyInstances.executionContext
    internal const val aRequestIssuingInstant = 0L
    internal const val anExecutionMaxDuration = 300L

    internal val aVarargSignature = Signature("ciao", 2, true)
    internal val varargArgumentList = anArgumentList + Truth.TRUE

    /** Utility function to assert [Compute.Request] contents are correct */
    internal fun Compute.Request<ExecutionContext>.assertContainsCorrectData(
        expectedSignature: Signature,
        expectedArguments: List<Term>,
        expectedContext: ExecutionContext,
        expectedRequestIssuingInstant: TimeInstant,
        expectedMaxDuration: TimeDuration
    ) {
        val toleranceInMillis = 10L // 10 ms
        assertEquals(expectedSignature, signature)
        assertEquals(expectedArguments, arguments)
        assertEquals(expectedContext, context)
        assertTrue("Actual issuing instant `$requestIssuingInstant` diverges more than `$toleranceInMillis` from expected one `$expectedRequestIssuingInstant`") {
            requestIssuingInstant - expectedRequestIssuingInstant < toleranceInMillis
        }
        assertEquals(expectedMaxDuration, executionMaxDuration)
    }

    // Response parameters
    internal val aResult = Empty.list()

    /** The response with [aResult] */
    internal val defaultResponse by lazy { Compute.Response(aResult) }

    /** Utility function to create a request with some default values */
    internal fun createRequest(
        signature: Signature = aSignature,
        arguments: List<Term> = anArgumentList,
        executionContext: ExecutionContext = anExecutionContext,
        requestIssuingInstant: TimeInstant = aRequestIssuingInstant,
        executionMaxDuration: TimeDuration = anExecutionMaxDuration
    ) = Compute.Request(signature, arguments, executionContext, requestIssuingInstant, executionMaxDuration)
}
