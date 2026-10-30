@file:Suppress("LongMethod") // a case table

package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.assertHasPredicateInAPI
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.stdlib.primitive.GetDurable
import it.unibo.tuprolog.solve.stdlib.primitive.GetEphemeral
import it.unibo.tuprolog.solve.stdlib.primitive.GetPersistent
import it.unibo.tuprolog.solve.stdlib.primitive.SetDurable
import it.unibo.tuprolog.solve.stdlib.primitive.SetEphemeral
import it.unibo.tuprolog.solve.stdlib.primitive.SetPersistent
import it.unibo.tuprolog.solve.yes

fun concurrentCustomDataContract() =
    concurrentContract(
        disabled =
            setOf(
                "custom-data/ephemeral-data", // todo need specific implementation to handle side effects
                "custom-data/durable-data", // todo need specific implementation to handle side effects
                "custom-data/persistent-data", // todo need specific implementation to handle side effects
            ),
    ) {
        scenario("custom-data/api") {
            val solver = solverWithDefaultBuiltins()

            solver.assertHasPredicateInAPI(GetPersistent)
            solver.assertHasPredicateInAPI(GetEphemeral)
            solver.assertHasPredicateInAPI(GetDurable)
            solver.assertHasPredicateInAPI(SetPersistent)
            solver.assertHasPredicateInAPI(SetEphemeral)
            solver.assertHasPredicateInAPI(SetDurable)
        }

        scenario("custom-data/ephemeral-data") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val setQuery = SetEphemeral.functor("key", 1)
                val getQuery = GetEphemeral.functor("key", X)
                val query = setQuery and (getQuery or getQuery)

                var solutions = fromSequence(solver.solve(setQuery, shortDuration))
                var expected = fromSequence(setQuery.yes())

                expected.assertingEquals(solutions)

                solutions = fromSequence(solver.solve(getQuery, shortDuration))
                expected = fromSequence(getQuery.no())

                expected.assertingEquals(solutions)

                solutions = fromSequence(solver.solve(query, shortDuration))
                expected = fromSequence(sequenceOf(query.yes(X to 1), query.no()))

                expected.assertingEquals(solutions)
            }
        }

        scenario("custom-data/durable-data") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val setQuery = SetDurable.functor("key", 1)
                val getQuery = GetDurable.functor("key", X)
                val query = setQuery and (getQuery or getQuery)

                var solutions = fromSequence(solver.solve(setQuery, shortDuration))
                var expected = fromSequence(setQuery.yes())

                expected.assertingEquals(solutions)

                solutions = fromSequence(solver.solve(getQuery, shortDuration))
                expected = fromSequence(getQuery.no())

                expected.assertingEquals(solutions)

                solutions = fromSequence(solver.solve(query, shortDuration))
                expected = fromSequence(sequenceOf(query.yes(X to 1), query.yes(X to 1)))

                expected.assertingEquals(solutions)
            }
        }

        scenario("custom-data/persistent-data") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val setQuery = SetPersistent.functor("key", 1)
                val getQuery = GetPersistent.functor("key", X)
                val query = setQuery and (getQuery or getQuery)

                var solutions = fromSequence(solver.solve(setQuery, shortDuration))
                var expected = fromSequence(setQuery.yes())

                expected.assertingEquals(solutions)

                solutions = fromSequence(solver.solve(getQuery, shortDuration))
                expected = fromSequence(getQuery.yes(X to 1))

                expected.assertingEquals(solutions)

                solutions = fromSequence(solver.solve(query, shortDuration))
                expected = fromSequence(sequenceOf(query.yes(X to 1), query.yes(X to 1)))

                expected.assertingEquals(solutions)
            }
        }
    }
