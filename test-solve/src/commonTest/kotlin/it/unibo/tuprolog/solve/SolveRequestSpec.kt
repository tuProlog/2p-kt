package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
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
import it.unibo.tuprolog.solve.testutils.SolveUtils.defaultRequestResponses
import it.unibo.tuprolog.solve.testutils.SolveUtils.defaultRequestSuccessResponse
import it.unibo.tuprolog.solve.testutils.SolveUtils.differentDynamicKB
import it.unibo.tuprolog.solve.testutils.SolveUtils.differentFlags
import it.unibo.tuprolog.solve.testutils.SolveUtils.differentLibraries
import it.unibo.tuprolog.solve.testutils.SolveUtils.differentStaticKB
import it.unibo.tuprolog.solve.testutils.SolveUtils.solutionException
import it.unibo.tuprolog.solve.testutils.SolveUtils.solutionSubstitution
import it.unibo.tuprolog.solve.testutils.SolveUtils.varargArgumentList
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

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

            assertEquals(aSignature, toBeTested.signature)
            assertEquals(anArgumentList, toBeTested.arguments)
            assertEquals(anExecutionContext, toBeTested.context)
            assertEquals(aRequestIssuingInstant, toBeTested.startTime)
            assertEquals(anExecutionMaxDuration, toBeTested.maxDuration)
        }

        test("request-default-values-correct") {
            val toBeTested = Solve.Request(aSignature, anArgumentList, anExecutionContext)

            val now = currentTimeInstant()
            assertTrue(now - toBeTested.startTime < TOLERANCE)
            assertTrue(abs(TimeDuration.MAX_VALUE - now - toBeTested.maxDuration) < TOLERANCE)
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
            assertFailsWith<TimeOutException> { createRequest(executionMaxDuration = -1) }
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

        test("reply-with-solution-creates-correct-response") {
            defaultRequestResponses.forEach {
                assertEquals(
                    createRequest().replyWith(
                        it.solution,
                        aSideEffectManager,
                    ) {
                        resetRuntime(differentLibraries)
                        resetFlags(differentFlags)
                        resetStaticKb(differentStaticKB)
                        resetDynamicKb(differentDynamicKB)
                    },
                    it,
                )
            }
        }

        test("reply-success-creates-correct-response") {
            assertEquals(
                createRequest().replySuccess(
                    solutionSubstitution,
                    aSideEffectManager,
                ) {
                    resetRuntime(differentLibraries)
                    resetFlags(differentFlags)
                    resetStaticKb(differentStaticKB)
                    resetDynamicKb(differentDynamicKB)
                },
                defaultRequestSuccessResponse,
            )
        }

        test("reply-fail-creates-correct-response") {
            assertEquals(
                createRequest().replyFail(
                    aSideEffectManager,
                ) {
                    resetRuntime(differentLibraries)
                    resetFlags(differentFlags)
                    resetStaticKb(differentStaticKB)
                    resetDynamicKb(differentDynamicKB)
                },
                defaultRequestFailedResponse,
            )
        }

        test("reply-exception-creates-correct-response") {
            assertEquals(
                createRequest().replyException(
                    solutionException,
                    aSideEffectManager,
                ) {
                    resetRuntime(differentLibraries)
                    resetFlags(differentFlags)
                    resetStaticKb(differentStaticKB)
                    resetDynamicKb(differentDynamicKB)
                },
                defaultRequestHaltedResponse,
            )
        }

        test("reply-with-boolean-creates-correct-response") {
            assertEquals(
                createRequest().replyWith(
                    true,
                    aSideEffectManager,
                ) {
                    resetRuntime(differentLibraries)
                    resetFlags(differentFlags)
                    resetStaticKb(differentStaticKB)
                    resetDynamicKb(differentDynamicKB)
                },
                defaultRequestSuccessResponse.copy(
                    solution =
                        (defaultRequestSuccessResponse.solution as Solution.Yes).copy(
                            substitution = Substitution.empty(),
                        ),
                ),
            )

            assertEquals(
                createRequest().replyWith(
                    false,
                    aSideEffectManager,
                ) {
                    resetRuntime(differentLibraries)
                    resetFlags(differentFlags)
                    resetStaticKb(differentStaticKB)
                    resetDynamicKb(differentDynamicKB)
                },
                defaultRequestFailedResponse,
            )
        }

        test("reply-with-unifier-creates-success-response") {
            assertEquals(
                defaultRequestSuccessResponse,
                createRequest().replyWith(solutionSubstitution as Substitution, aSideEffectManager) {
                    sideEffects.addAll(defaultRequestSuccessResponse.sideEffects)
                },
            )
        }

        test("reply-with-failed-substitution-creates-failed-response") {
            assertEquals(
                defaultRequestFailedResponse,
                createRequest().replyWith(Substitution.failed(), aSideEffectManager) {
                    sideEffects.addAll(defaultRequestFailedResponse.sideEffects)
                },
            )
        }

        withTests(nameFn = { "reply-with-vararg-side-effects/${it.id}" }, varargReplyCases) { case ->
            val expected = case.expected()
            assertEquals(expected, createRequest().(case.reply)(expected.sideEffects.toTypedArray()))
        }
    })

private const val TOLERANCE = 10L

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
