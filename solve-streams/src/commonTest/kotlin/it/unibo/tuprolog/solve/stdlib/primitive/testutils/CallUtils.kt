package it.unibo.tuprolog.solve.stdlib.primitive.testutils

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleFactTheory
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleFactTheoryNotableGoalToSolutions
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.solver.StreamsExecutionContext
import it.unibo.tuprolog.solve.solver.fsm.impl.StateEnd
import it.unibo.tuprolog.solve.solver.fsm.impl.StateGoalEvaluation
import it.unibo.tuprolog.solve.stdlib.primitive.Call
import it.unibo.tuprolog.solve.stdlib.primitive.Conjunction
import it.unibo.tuprolog.solve.stdlib.primitive.Cut
import it.unibo.tuprolog.solve.stdlib.primitive.Throw
import it.unibo.tuprolog.solve.testutils.SolverTestUtils.createSolveRequest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.collections.listOf as ktListOf

/**
 * Utils singleton to help testing [Call]
 *
 * @author Enrico
 */
internal object CallUtils {

    private val aContext = DummyInstances.executionContext

    /**
     * Call primitive working examples, with expected responses
     *
     * Contained requests:
     * - `call(true)` **will result in** `Yes()`
     * - `call((true, true))` **will result in** `Yes()`
     * - `call('!')` **will result in** `Yes()`
     * - `call(f(A))` against [factDatabase][simpleFactTheory]  **will result in** `Yes(A -> a)`
     * - `call(g(A))` against [factDatabase][simpleFactTheory]  **will result in** `Yes(A -> a), Yes(A -> b)`
     * - `call(h(A))` against [factDatabase][simpleFactTheory]  **will result in** `Yes(A -> a), Yes(A -> b), Yes(A -> c)`
     * - `call((f(A), '!'))` against [factDatabase][simpleFactTheory]  **will result in** `Yes(A -> a)`
     * - `call((g(A), '!'))` against [factDatabase][simpleFactTheory]  **will result in** `Yes(A -> a)`
     * - `call((h(A), '!'))` against [factDatabase][simpleFactTheory]  **will result in** `Yes(A -> a)`
     */
    internal val requestSolutionMap by lazy {
        prolog {
            mapOf(
                Call.functor(true)
                    .hasSolutions({ yes() }),
                Call.functor("true" and "true")
                    .hasSolutions({ yes() }),
                Call.functor("!")
                    .hasSolutions({ yes() }),
                *simpleFactTheoryNotableGoalToSolutions.map { (goal, solutionList) ->
                    Call.functor(goal).run { to(solutionList.changeQueriesTo(this)) }
                }.toTypedArray(),
                *simpleFactTheoryNotableGoalToSolutions.map { (goal, solutionList) ->
                    Call.functor(goal and "!").run { to(solutionList.subList(0, 1).changeQueriesTo(this)) }
                }.toTypedArray()
            ).mapKeys { (query, _) ->
                createSolveRequest(
                    query, database = simpleFactTheory,
                    primitives = mapOf(*ktListOf(Call, Cut, Conjunction).map { it.descriptionPair }.toTypedArray())
                )
            }
        }
    }

    /**
     * Requests that should throw errors
     *
     * Contained requests:
     *
     * - `call(X)` **will result in** `InstantiationError()`
     * - `call((true, 1))`  **will result in** `TypeError()`
     */
    internal val requestToErrorSolutionMap by lazy {
        prolog {
            mapOf(
                Call.functor("X").hasSolutions({
                    halt(InstantiationError(context = aContext, extraData = varOf("X")))
                }),
                Call.functor(true and 1).hasSolutions({
                    halt(
                        TypeError(
                            context = aContext,
                            expectedType = TypeError.Expected.CALLABLE,
                            actualValue = true and 1
                        )
                    )
                })
            ).mapKeys { (query, _) ->
                createSolveRequest(
                    query,
                    primitives = mapOf(
                        *ktListOf(Call, Cut, Throw, Conjunction)
                            .map { it.descriptionPair }.toTypedArray()
                    )
                )
            }
        }
    }

    /**
     * A request to test that [Call] limits [Cut] to have effect only inside its goal; `call/1` is said to be *opaque* (or not transparent) to cut.
     *
     * - `call(g(A), call('!'))` against [factDatabase][simpleFactTheory]  **will result in** `Yes(A -> a), Yes(A -> b)`
     */
    internal val requestToSolutionOfCallWithCut by lazy {
        prolog {
            mapOf(
                *simpleFactTheoryNotableGoalToSolutions.map { (goal, solutionList) ->
                    Call.functor(goal and Call.functor("!")).run {
                        to(solutionList.changeQueriesTo(this))
                    }
                }.toTypedArray()
            ).mapKeys { (query, _) ->
                createSolveRequest(
                    query, database = simpleFactTheory,
                    primitives = mapOf(*ktListOf(Call, Cut, Conjunction).map { it.descriptionPair }.toTypedArray())
                )
            }
        }
    }

    /**
     * Utility function to test whether the cause of errors generated is correctly filled
     *
     * It passes request to StateGoalEvaluation, then it executes the primitive exercising the error situation;
     * in the end the generated solution's error chain is checked to match with [expectedErrorSolution]'s chain
     */
    internal fun assertErrorCauseChainComputedCorrectly(
        request: Solve.Request<StreamsExecutionContext>,
        expectedErrorSolution: Solution.Halt
    ) {
        val nextState = StateGoalEvaluation(request).behave().toList().single()

        assertEquals(StateEnd.Halt::class, nextState::class)
        assertEquals(expectedErrorSolution.exception::class, (nextState as StateEnd.Halt).exception::class)

        var expectedCause = expectedErrorSolution.exception.cause
        var actualCause = nextState.exception.cause

        while (expectedCause != null) {
            val expectedCauseStruct = (expectedCause as? PrologError)?.errorStruct
            val actualCauseStruct = (actualCause as? PrologError)?.errorStruct

            assertNotNull(expectedCauseStruct)
            assertNotNull(actualCauseStruct)

            assertTrue("Expected `$expectedCauseStruct` not structurally equals to actual `$actualCauseStruct`") {
                expectedCauseStruct.structurallyEquals(actualCauseStruct)
            }

            expectedCause = expectedCause.cause
            actualCause = actualCause?.cause
        }
    }
}
