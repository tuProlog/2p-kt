package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.stdlib.primitive.GetDurable
import it.unibo.tuprolog.solve.stdlib.primitive.GetEphemeral
import it.unibo.tuprolog.solve.stdlib.primitive.GetPersistent
import it.unibo.tuprolog.solve.stdlib.primitive.SetDurable
import it.unibo.tuprolog.solve.stdlib.primitive.SetEphemeral
import it.unibo.tuprolog.solve.stdlib.primitive.SetPersistent

/** Conformance tests for the non-standard `get_ephemeral/2`, `get_durable/2`, `get_persistent/2`, `set_ephemeral/2`, `set_durable/2` and `set_persistent/2` built-ins, which read/write the three tiers of [it.unibo.tuprolog.solve.data.CustomDataStore] attached to a [Solver]'s execution context. */
fun customDataContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    // Tests that a fresh [Solver]'s API exposes all six custom-data get/set primitives.
    scenario("custom-data/api") {
        val solver = solverFactory.solverWithDefaultBuiltins()

        solver.assertHasPredicateInAPI(GetPersistent)
        solver.assertHasPredicateInAPI(GetEphemeral)
        solver.assertHasPredicateInAPI(GetDurable)
        solver.assertHasPredicateInAPI(SetPersistent)
        solver.assertHasPredicateInAPI(SetEphemeral)
        solver.assertHasPredicateInAPI(SetDurable)
    }

    // Tests that, within a single query `set_ephemeral(key, 1), (get_ephemeral(key, X) ; get_ephemeral(key, X))`,
    // the first `get_ephemeral/2` succeeds binding `X` to `1`, but backtracking into the disjunction to retry
    // `get_ephemeral/2` a second time fails: ephemeral data does not survive backtracking within the same query.
    scenario("custom-data/ephemeral-data") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val setQuery = SetEphemeral.functor("key", 1)
            val getQuery = GetEphemeral.functor("key", X)
            val query = setQuery and (getQuery or getQuery)

            assertSolutionEquals(
                listOf(setQuery.yes()),
                solver.solve(setQuery, shortDuration).toList(),
            )

            assertSolutionEquals(
                listOf(getQuery.no()),
                solver.solve(getQuery, shortDuration).toList(),
            )

            assertSolutionEquals(
                listOf(
                    query.yes(X to 1),
                    query.no(),
                ),
                solver.solve(query, shortDuration).toList(),
            )
        }
    }

    // Same query shape as [testEphemeralData] but using `set_durable/2`/`get_durable/2`: unlike ephemeral data,
    // durable data survives backtracking, so *both* attempts at `get_durable/2` succeed, binding `X` to `1`.
    scenario("custom-data/durable-data") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val setQuery = SetDurable.functor("key", 1)
            val getQuery = GetDurable.functor("key", X)
            val query = setQuery and (getQuery or getQuery)

            assertSolutionEquals(
                listOf(setQuery.yes()),
                solver.solve(setQuery, shortDuration).toList(),
            )

            assertSolutionEquals(
                listOf(getQuery.no()),
                solver.solve(getQuery, shortDuration).toList(),
            )

            assertSolutionEquals(
                listOf(
                    query.yes(X to 1),
                    query.yes(X to 1),
                ),
                solver.solve(query, shortDuration).toList(),
            )
        }
    }

    // Tests that persistent data survives across separate `solve` calls on the same [Solver]: after solving
    // `set_persistent(key, 1)` in one call, a *separate* `solve` call for `get_persistent(key, X)` still succeeds
    // (binding `X` to `1`), and a query combining both within a single call behaves like [testDurableData].
    scenario("custom-data/persistent-data") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val setQuery = SetPersistent.functor("key", 1)
            val getQuery = GetPersistent.functor("key", X)
            val query = setQuery and (getQuery or getQuery)

            assertSolutionEquals(
                listOf(setQuery.yes()),
                solver.solve(setQuery, shortDuration).toList(),
            )

            assertSolutionEquals(
                listOf(getQuery.yes(X to 1)),
                solver.solve(getQuery, shortDuration).toList(),
            )

            assertSolutionEquals(
                listOf(
                    query.yes(X to 1),
                    query.yes(X to 1),
                ),
                solver.solve(query, shortDuration).toList(),
            )
        }
    }
}
