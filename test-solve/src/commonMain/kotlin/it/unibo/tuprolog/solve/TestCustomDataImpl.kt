package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.stdlib.primitive.GetDurable
import it.unibo.tuprolog.solve.stdlib.primitive.GetEphemeral
import it.unibo.tuprolog.solve.stdlib.primitive.GetPersistent
import it.unibo.tuprolog.solve.stdlib.primitive.SetDurable
import it.unibo.tuprolog.solve.stdlib.primitive.SetEphemeral
import it.unibo.tuprolog.solve.stdlib.primitive.SetPersistent

class TestCustomDataImpl(
    private val solverFactory: SolverFactory,
) : TestCustomData {
    override fun testApi() {
        val solver = solverFactory.solverWithDefaultBuiltins()

        solver.assertHasPredicateInAPI(GetPersistent)
        solver.assertHasPredicateInAPI(GetEphemeral)
        solver.assertHasPredicateInAPI(GetDurable)
        solver.assertHasPredicateInAPI(SetPersistent)
        solver.assertHasPredicateInAPI(SetEphemeral)
        solver.assertHasPredicateInAPI(SetDurable)
    }

    override fun testEphemeralData() {
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

    override fun testDurableData() {
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

    override fun testPersistentData() {
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
