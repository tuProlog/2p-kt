package it.unibo.tuprolog.solve

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.sideffects.SideEffect
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
import it.unibo.tuprolog.solve.testutils.SolveUtils.defaultRequestSuccessResponse
import it.unibo.tuprolog.solve.testutils.SolveUtils.differentDynamicKB
import it.unibo.tuprolog.solve.testutils.SolveUtils.differentFlags
import it.unibo.tuprolog.solve.testutils.SolveUtils.differentLibraries
import it.unibo.tuprolog.solve.testutils.SolveUtils.differentStaticKB
import it.unibo.tuprolog.solve.testutils.SolveUtils.solutionException
import it.unibo.tuprolog.solve.testutils.SolveUtils.solutionSubstitution
import it.unibo.tuprolog.solve.testutils.SolveUtils.varargArgumentList
import kotlin.math.abs

/**
 * Test class for [Solve.Request]
 *
 * @author Enrico
 */
class SolveRequestSpec :
    FunSpec({
        test("request-inserted-data-correct") {
            val toBeTested =
                Solve.Request(
                    aSignature,
                    anArgumentList,
                    anExecutionContext,
                    aRequestIssuingInstant,
                    anExecutionMaxDuration,
                )

            toBeTested.signature shouldBe aSignature
            toBeTested.arguments shouldBe anArgumentList
            toBeTested.context shouldBe anExecutionContext
            toBeTested.startTime shouldBe aRequestIssuingInstant
            toBeTested.maxDuration shouldBe anExecutionMaxDuration
        }

        test("request-default-values-correct") {
            val toBeTested = Solve.Request(aSignature, anArgumentList, anExecutionContext)

            val now = currentTimeInstant()
            (now - toBeTested.startTime < TOLERANCE).shouldBeTrue()
            (abs(TimeDuration.MAX_VALUE - now - toBeTested.maxDuration) < TOLERANCE).shouldBeTrue()
        }

        test("request-constructor-complains-with-wrong-arity-and-arguments-count") {
            shouldThrow<IllegalArgumentException> { createRequest(arguments = emptyList()) }
            shouldThrow<IllegalArgumentException> { createRequest(arguments = anArgumentList + Truth.FAIL) }
        }

        test("request-constructor-complains-with-less-than-arity-arguments-if-vararg-signature") {
            shouldThrow<IllegalArgumentException> { createRequest(aVarargSignature, emptyList()) }
        }

        test("request-constructor-permits-more-than-arity-arguments-if-signature-vararg") {
            createRequest(aVarargSignature, anArgumentList + Truth.FAIL)
        }

        test("request-constructor-complains-with-negative-max-duration") {
            createRequest(executionMaxDuration = 0)
            shouldThrow<TimeOutException> { createRequest(executionMaxDuration = -1) }
        }

        test("request-constructor-complains-with-negative-request-issuing-instant") {
            createRequest(requestIssuingInstant = 0)
            shouldThrow<IllegalArgumentException> { createRequest(requestIssuingInstant = -1) }
        }

        test("request-computes-correctly-query-struct") {
            createRequest(aSignature, anArgumentList).query shouldBe Struct.of(aSignature.name, anArgumentList)
            createRequest(aVarargSignature, varargArgumentList).query shouldBe
                Struct.of(aVarargSignature.name, varargArgumentList)
        }

        test("equals-works-as-expected") {
            val aRequest = createRequest()
            aRequest shouldBe aRequest.copy()
        }

        withTests(nameFn = { "reply-with-solution/${it.id}" }, responseCases) { case ->
            val expected = case.expected()
            expected shouldBe (
                createRequest().replyWith(
                    expected.solution,
                    aSideEffectManager,
                ) {
                    resetRuntime(differentLibraries)
                    resetFlags(differentFlags)
                    resetStaticKb(differentStaticKB)
                    resetDynamicKb(differentDynamicKB)
                }
            )
        }

        test("reply-success-creates-correct-response") {
            defaultRequestSuccessResponse shouldBe (
                createRequest().replySuccess(
                    solutionSubstitution,
                    aSideEffectManager,
                ) {
                    resetRuntime(differentLibraries)
                    resetFlags(differentFlags)
                    resetStaticKb(differentStaticKB)
                    resetDynamicKb(differentDynamicKB)
                }
            )
        }

        test("reply-fail-creates-correct-response") {
            defaultRequestFailedResponse shouldBe (
                createRequest().replyFail(
                    aSideEffectManager,
                ) {
                    resetRuntime(differentLibraries)
                    resetFlags(differentFlags)
                    resetStaticKb(differentStaticKB)
                    resetDynamicKb(differentDynamicKB)
                }
            )
        }

        test("reply-exception-creates-correct-response") {
            defaultRequestHaltedResponse shouldBe (
                createRequest().replyException(
                    solutionException,
                    aSideEffectManager,
                ) {
                    resetRuntime(differentLibraries)
                    resetFlags(differentFlags)
                    resetStaticKb(differentStaticKB)
                    resetDynamicKb(differentDynamicKB)
                }
            )
        }

        test("reply-with-boolean-creates-correct-response") {
            defaultRequestSuccessResponse.copy(
                solution =
                    (defaultRequestSuccessResponse.solution as Solution.Yes).copy(
                        substitution = Substitution.empty(),
                    ),
            ) shouldBe (
                createRequest().replyWith(
                    true,
                    aSideEffectManager,
                ) {
                    resetRuntime(differentLibraries)
                    resetFlags(differentFlags)
                    resetStaticKb(differentStaticKB)
                    resetDynamicKb(differentDynamicKB)
                }
            )

            defaultRequestFailedResponse shouldBe (
                createRequest().replyWith(
                    false,
                    aSideEffectManager,
                ) {
                    resetRuntime(differentLibraries)
                    resetFlags(differentFlags)
                    resetStaticKb(differentStaticKB)
                    resetDynamicKb(differentDynamicKB)
                }
            )
        }

        test("reply-with-unifier-creates-success-response") {
            (
                createRequest().replyWith(solutionSubstitution as Substitution, aSideEffectManager) {
                    sideEffects.addAll(defaultRequestSuccessResponse.sideEffects)
                }
            ) shouldBe defaultRequestSuccessResponse
        }

        test("reply-with-failed-substitution-creates-failed-response") {
            (
                createRequest().replyWith(Substitution.failed(), aSideEffectManager) {
                    sideEffects.addAll(defaultRequestFailedResponse.sideEffects)
                }
            ) shouldBe defaultRequestFailedResponse
        }

        withTests(nameFn = { "reply-with-vararg-side-effects/${it.id}" }, varargReplyCases) { case ->
            val expected = case.expected()
            createRequest().(case.reply)(expected.sideEffects.toTypedArray()) shouldBe expected
        }
    })

private const val TOLERANCE = 10L

private class ResponseCase(
    val id: String,
    val expected: () -> Solve.Response,
)

private val responseCases =
    listOf(
        ResponseCase("success") { defaultRequestSuccessResponse },
        ResponseCase("fail") { defaultRequestFailedResponse },
        ResponseCase("halt") { defaultRequestHaltedResponse },
    )

private class VarargReplyCase(
    val id: String,
    val expected: () -> Solve.Response,
    val reply: Solve.Request<ExecutionContext>.(Array<SideEffect>) -> Solve.Response,
)

private val varargReplyCases =
    listOf(
        VarargReplyCase("success", { defaultRequestSuccessResponse }) {
            replySuccess(solutionSubstitution, aSideEffectManager, *it)
        },
        VarargReplyCase("fail", { defaultRequestFailedResponse }) { replyFail(aSideEffectManager, *it) },
        VarargReplyCase("exception", { defaultRequestHaltedResponse }) {
            replyException(solutionException, aSideEffectManager, *it)
        },
        VarargReplyCase("with-solution", { defaultRequestSuccessResponse }) {
            replyWith(defaultRequestSuccessResponse.solution, aSideEffectManager, *it)
        },
        VarargReplyCase("with-substitution", { defaultRequestSuccessResponse }) {
            replyWith(solutionSubstitution as Substitution, aSideEffectManager, *it)
        },
    )
