package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.assertHasPredicateInAPI
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.stdlib.primitive.GetDurable
import it.unibo.tuprolog.solve.stdlib.primitive.GetEphemeral
import it.unibo.tuprolog.solve.stdlib.primitive.GetPersistent
import it.unibo.tuprolog.solve.stdlib.primitive.SetDurable
import it.unibo.tuprolog.solve.stdlib.primitive.SetEphemeral
import it.unibo.tuprolog.solve.stdlib.primitive.SetPersistent
import it.unibo.tuprolog.solve.yes

interface TestConcurrentCustomData<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testApi() {
        val solver = solverWithDefaultBuiltins()

        solver.assertHasPredicateInAPI(GetPersistent)
        solver.assertHasPredicateInAPI(GetEphemeral)
        solver.assertHasPredicateInAPI(GetDurable)
        solver.assertHasPredicateInAPI(SetPersistent)
        solver.assertHasPredicateInAPI(SetEphemeral)
        solver.assertHasPredicateInAPI(SetDurable)
    }

    fun testEphemeralData() {
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

    fun testDurableData() {
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

    fun testPersistentData() {
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
