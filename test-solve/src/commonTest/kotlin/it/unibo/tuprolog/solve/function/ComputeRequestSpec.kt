package it.unibo.tuprolog.solve.function

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.currentTimeInstant
import it.unibo.tuprolog.solve.function.testutils.ComputeUtils.aRequestIssuingInstant
import it.unibo.tuprolog.solve.function.testutils.ComputeUtils.aResult
import it.unibo.tuprolog.solve.function.testutils.ComputeUtils.aSignature
import it.unibo.tuprolog.solve.function.testutils.ComputeUtils.aVarargSignature
import it.unibo.tuprolog.solve.function.testutils.ComputeUtils.anArgumentList
import it.unibo.tuprolog.solve.function.testutils.ComputeUtils.anExecutionContext
import it.unibo.tuprolog.solve.function.testutils.ComputeUtils.anExecutionMaxDuration
import it.unibo.tuprolog.solve.function.testutils.ComputeUtils.assertContainsCorrectData
import it.unibo.tuprolog.solve.function.testutils.ComputeUtils.createRequest
import it.unibo.tuprolog.solve.function.testutils.ComputeUtils.defaultResponse
import it.unibo.tuprolog.solve.function.testutils.ComputeUtils.varargArgumentList
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [Compute.Request]
 *
 * @author Enrico
 */
class ComputeRequestSpec :
    FunSpec({
        test("request-inserted-data-correct") {
            val toBeTested =
                Compute.Request(
                    aSignature,
                    anArgumentList,
                    anExecutionContext,
                    aRequestIssuingInstant,
                    anExecutionMaxDuration,
                )

            toBeTested.assertContainsCorrectData(
                aSignature,
                anArgumentList,
                anExecutionContext,
                aRequestIssuingInstant,
                anExecutionMaxDuration,
            )
        }

        test("request-default-values-correct") {
            val toBeTested = Compute.Request(aSignature, anArgumentList, anExecutionContext)

            toBeTested.assertContainsCorrectData(
                aSignature,
                anArgumentList,
                anExecutionContext,
                currentTimeInstant(),
                TimeDuration.MAX_VALUE,
            )
        }

        test("request-constructor-complains-with-wrong-arity-and-arguments-count") {
            assertFailsWith<IllegalArgumentException> { createRequest(arguments = emptyList()) }
            assertFailsWith<IllegalArgumentException> { createRequest(arguments = anArgumentList + Truth.FAIL) }
        }

        test("request-constructor-complains-with-less-than-arity-arguments-if-vararg-signature") {
            assertFailsWith<IllegalArgumentException> { createRequest(aVarargSignature, emptyList()) }
        }

        test("request-constructor-permits-more-than-arity-arguments-if-signature-vararg") {
            createRequest(aVarargSignature, anArgumentList + Truth.FAIL)
        }

        test("request-constructor-complains-with-negative-max-duration") {
            createRequest(executionMaxDuration = 0)
            assertFailsWith<IllegalArgumentException> { createRequest(executionMaxDuration = -1) }
        }

        test("request-constructor-complains-with-negative-request-issuing-instant") {
            createRequest(requestIssuingInstant = 0)
            assertFailsWith<IllegalArgumentException> { createRequest(requestIssuingInstant = -1) }
        }

        test("request-computes-correctly-query-struct") {
            assertEquals(Struct.of(aSignature.name, anArgumentList), createRequest(aSignature, anArgumentList).query)
            assertEquals(
                Struct.of(aVarargSignature.name, varargArgumentList),
                createRequest(aVarargSignature, varargArgumentList).query,
            )
        }

        test("equals-works-as-expected") {
            val aRequest = createRequest()
            assertEquals(aRequest.copy(), aRequest)
        }

        test("reply-with-creates-correct-response") {
            assertEquals(createRequest().replyWith(aResult), defaultResponse)
        }
    })
