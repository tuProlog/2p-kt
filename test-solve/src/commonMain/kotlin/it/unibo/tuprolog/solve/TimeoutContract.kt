package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.theory.LogicProgrammingScope
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.stdlib.primitive.FindAll
import it.unibo.tuprolog.solve.stdlib.primitive.Sleep

/** Conformance tests for `Solver.solve(goal, maxDuration)`'s timeout enforcement: checking that a computation exceeding its allotted [it.unibo.tuprolog.solve.TimeDuration] halts with a [it.unibo.tuprolog.solve.exception.TimeOutException], both for a plainly slow goal (`sleep/1`) and for non-terminating all-solutions collectors (`findall/3`, `bagof/3`, `setof/3`) fed by an infinite generator. */
fun timeoutContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    // Tests the query
    // ```prolog
    // ?- sleep(MediumDuration).
    // ```
    // solved with [shortDuration] as the max duration: since the sleep alone exceeds the budget, the sole result is
    // a [Solution.Halt] wrapping a [it.unibo.tuprolog.solve.exception.TimeOutException] for [shortDuration].
    scenario("timeout/sleep") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            solver.assertHasPredicateInAPI(Sleep)

            val query = "sleep"(mediumDuration)

            val solutions = solver.solveList(query, shortDuration)

            assertSolutionEquals(
                listOf<Solution>(
                    query.halt(
                        TimeOutException(
                            context = DummyInstances.executionContext,
                            exceededDuration = shortDuration,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    // Tests the query
    // ```prolog
    // ?- findall(N, nat(N), L).
    // ```
    // against a theory recursively defining the (infinite) natural numbers `nat(z). nat(s(Z)) :- nat(Z).`, solved
    // with [shortDuration] as the max duration: since `findall/3` must exhaust every solution of its generator
    // before returning, and the generator never terminates, the sole result is a [Solution.Halt] wrapping a
    // [it.unibo.tuprolog.solve.exception.TimeOutException] for [shortDuration].
    scenario("timeout/infinite-find-all") {
        testInfiniteCollectingGoal {
            "findall"(N, "nat"(N), L)
        }
    }

    // Same as [testInfiniteFindAll], but with `bagof(N, nat(N), L)` in place of `findall/3`.
    scenario("timeout/infinite-bag-of") {
        testInfiniteCollectingGoal {
            "bagof"(N, "nat"(N), L)
        }
    }

    // Same as [testInfiniteFindAll], but with `setof(N, nat(N), L)` in place of `findall/3`.
    scenario("timeout/infinite-set-of") {
        testInfiniteCollectingGoal {
            "setof"(N, "nat"(N), L)
        }
    }
}

private fun ContractScope.testInfiniteCollectingGoal(goalProvider: LogicProgrammingScope.() -> Struct) {
    logicProgramming {
        val solver =
            solverFactory.solverWithDefaultBuiltins(
                staticKb =
                    theoryOf(
                        fact { "nat"("z") },
                        rule { "nat"("s"(Z)) impliedBy "nat"(Z) },
                    ),
            )
        solver.assertHasPredicateInAPI(FindAll)

        val query = goalProvider()

        val solutions = solver.solveList(query, shortDuration)

        assertSolutionEquals(
            listOf<Solution>(
                query.halt(
                    TimeOutException(context = DummyInstances.executionContext, exceededDuration = shortDuration),
                ),
            ),
            solutions,
        )
    }
}
