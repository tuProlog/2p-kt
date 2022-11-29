package it.unibo.tuprolog.solve.streams.primitive.testutils

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.catchAndThrowTheoryExample
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.catchAndThrowTheoryExampleNotableGoalToSolution
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.TestingClauseTheories.catchTestingGoalsToSolutions
import it.unibo.tuprolog.solve.changeQueriesTo
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Call
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Catch
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Conjunction
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Throw
import it.unibo.tuprolog.solve.streams.testutils.SolverTestUtils.createSolveRequest
import it.unibo.tuprolog.solve.yes
import kotlin.collections.listOf as ktListOf

/**
 * Utils singleton to help testing [Catch]
 *
 * @author Enrico
 */
internal object CatchUtils {

    /**
     * Catch primitive examples, with expected responses
     *
     * Contained requests:
     * - `catch(true, _, fail).` **will result in** `Yes()`
     * - `catch(catch(throw(external(deepBall)), internal(I), fail), external(E), true).` **will result in** `Yes(E -> deepBall)`
     * - `catch(throw(first), X, throw(second)).` **will result in** `Halt()`
     * - `catch(throw(hello), X, true).` **will result in** `Yes(X -> hello)`
     * - `catch((throw(hello), fail), X, true).`  **will result in** `Yes(X -> hello)`
     * - Plus all [CallUtils.requestSolutionMap] in the form `catch(callGoal, _, fail).` with same result as `callGoal`
     * - Plus all [CallUtils.requestSolutionMap] in the form `catch(callGoalArgument, _, fail).` with same result as `callArgumentGoal`
     * - Plus all [CallUtils.requestToErrorSolutionMap] in the form `catch(callGoal, X, true)` resulting in `X` to be bound to the error struct thrown
     */
    internal val requestSolutionMap by lazy {
        mapOf(
            *catchTestingGoalsToSolutions.map { (goal, solutionList) ->
                createSolveRequest(
                    goal,
                    primitives = mapOf(*ktListOf(Call, Catch, Conjunction, Throw).map { it.descriptionPair }.toTypedArray())
                ) to solutionList
            }.toTypedArray(),

            *CallUtils.requestSolutionMap.flatMap { (callRequest, solutions) ->
                ktListOf(
                    logicProgramming { Catch.functor(callRequest.query, `_`, false) },
                    logicProgramming { Catch.functor(callRequest.arguments.single(), `_`, false) }
                ).map {
                    with(callRequest.context.libraries) {
                        createSolveRequest(it, clauses, primitives + Catch.descriptionPair)
                    } to solutions.changeQueriesTo(it)
                }
            }.toTypedArray(),

            *CallUtils.requestToErrorSolutionMap.map { (callRequest, solutions) ->
                val updatedPrimitives = callRequest.context.libraries.primitives + Catch.descriptionPair
                logicProgramming {
                    Catch.functor(callRequest.arguments.single(), "X", true).run {
                        createSolveRequest(this, callRequest.context.libraries.clauses, updatedPrimitives) to
                            solutions.map {
                                yes("X" to (it.exception as LogicError).errorStruct)
                            }
                    }
                }
            }.toTypedArray()
        )
    }

    /**
     * Prolog standard examples for `catch/3` primitive
     *
     * Contains those requests against [catchAndThrowTheoryExample]:
     *
     * - `catch(p, X, true)` **will result in** `Yes(), Yes(X -> b)`
     * - `catch(q, C, true)` **will result in** `Yes(C -> c)`
     * - `catch(throw(exit(1)), exit(X), true).` **will result in** `Yes(X -> 1)`
     * - `catch(throw(true), X, X).` **will result in** `Yes(X -> true)`
     * - `catch(throw(fail), X, X).` **will result in** `No()`
     */
    internal val prologStandardCatchExamples by lazy {
        catchAndThrowTheoryExampleNotableGoalToSolution
            .filter { (_, solutionList) -> solutionList.none { it is Solution.Halt } }
            .map { (goal, solutionList) ->
                createSolveRequest(
                    goal,
                    primitives = mapOf(
                        *ktListOf(Call, Catch, Conjunction, Throw).map { it.descriptionPair }.toTypedArray()
                    ),
                    database = catchAndThrowTheoryExample
                ) to solutionList
            }
    }
}
