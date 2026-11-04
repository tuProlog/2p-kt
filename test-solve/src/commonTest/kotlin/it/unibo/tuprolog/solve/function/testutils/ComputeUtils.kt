package it.unibo.tuprolog.solve.function.testutils

import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.function.Compute

/**
 * Utils singleton to help testing [Compute]
 *
 * @author Enrico
 */
@Suppress("ConstPropertyName", "ktlint:standard:property-naming")
internal object ComputeUtils {
    // Request parameters
    internal val aSignature = Signature("ciao", 2)
    internal val anArgumentList = listOf(Atom.of("a"), Truth.TRUE)
    internal val anExecutionContext = DummyInstances.executionContext

    internal val aVarargSignature = Signature("ciao", 2, true)
    internal val varargArgumentList = anArgumentList + Truth.TRUE

    /** Utility function to assert [Compute.Request] contents are correct */
    internal fun Compute.Request<ExecutionContext>.assertContainsCorrectData(
        expectedSignature: Signature,
        expectedArguments: List<Term>,
        expectedContext: ExecutionContext,
    ) {
        signature shouldBe expectedSignature
        arguments shouldBe expectedArguments
        context shouldBe expectedContext
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
    ) = Compute.Request(signature, arguments, executionContext)
}
