package it.unibo.tuprolog.solve.streams.primitive.testutils

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleFactTheory
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleFactTheoryNotableGoalToSolutions
import it.unibo.tuprolog.solve.changeQueriesTo
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.hasSolutions
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.streams.solver.StreamsExecutionContext
import it.unibo.tuprolog.solve.streams.solver.fsm.impl.StateEnd
import it.unibo.tuprolog.solve.streams.solver.fsm.impl.StateGoalEvaluation
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Call
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Conjunction
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Cut
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Throw
import it.unibo.tuprolog.solve.streams.testutils.SolverTestUtils.createSolveRequest
import it.unibo.tuprolog.solve.yes
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

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
        logicProgramming {
            mapOf(
                Call.functor(true).hasSolutions({ yes() }),
                Call.functor("true" and "true").hasSolutions({ yes() }),
                Call.functor("!").hasSolutions({ yes() }),
                *simpleFactTheoryNotableGoalToSolutions
                    .map { (goal, solutionList) ->
                        Call.functor(goal).run { to(solutionList.changeQueriesTo(this)) }
                    }.toTypedArray(),
                *simpleFactTheoryNotableGoalToSolutions
                    .map { (goal, solutionList) ->
                        Call.functor(goal and "!").run { to(solutionList.subList(0, 1).changeQueriesTo(this)) }
                    }.toTypedArray(),
            ).mapKeys { (query, _) ->
                createSolveRequest(
                    query,
                    database = simpleFactTheory,
                    primitives = listOf(Call, Cut, Conjunction).associate { it.descriptionPair },
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
        logicProgramming {
            mapOf(
                Call.functor("X").hasSolutions({
                    halt(InstantiationError(context = aContext, extraData = varOf("X")))
                }),
                Call.functor(true and 1).hasSolutions({
                    halt(
                        TypeError(
                            context = aContext,
                            expectedType = TypeError.Expected.CALLABLE,
                            culprit = true and 1,
                        ),
                    )
                }),
            ).mapKeys { (query, _) ->
                createSolveRequest(
                    query,
                    primitives = listOf(Call, Cut, Throw, Conjunction).associate { it.descriptionPair },
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
        logicProgramming {
            mapOf(
                *simpleFactTheoryNotableGoalToSolutions
                    .map { (goal, solutionList) ->
                        Call.functor(goal and Call.functor("!")).run {
                            to(solutionList.changeQueriesTo(this))
                        }
                    }.toTypedArray(),
            ).mapKeys { (query, _) ->
                createSolveRequest(
                    query,
                    database = simpleFactTheory,
                    primitives = listOf(Call, Cut, Conjunction).associate { it.descriptionPair },
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
        expectedErrorSolution: Solution.Halt,
    ) {
        val nextState = StateGoalEvaluation(request).behave().toList().single()

        assertEquals(StateEnd.Halt::class, nextState::class)
        assertEquals(expectedErrorSolution.exception::class, (nextState as StateEnd.Halt).exception::class)

        var expectedCause = expectedErrorSolution.exception.cause
        var actualCause = nextState.exception.cause

        while (expectedCause != null) {
            val expectedCauseStruct = (expectedCause as? LogicError)?.errorStruct
            val actualCauseStruct = (actualCause as? LogicError)?.errorStruct

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
