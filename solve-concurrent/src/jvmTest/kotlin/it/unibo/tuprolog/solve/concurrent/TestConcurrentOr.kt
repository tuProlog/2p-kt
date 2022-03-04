package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentOr<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    fun testTrueOrFalse() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = true or fail
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(sequenceOf(query.yes(), query.no()))

            expected.assertingEquals(solutions)
        }
    }

    fun testCutFalseOrTrue() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = "!" and fail or true
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testCutCall() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = "!" or call(3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testCutAssignedValue() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = ("X" eq 1 and "!") or ("X" eq 2)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 1))

            expected.assertingEquals(solutions)
        }
    }

    fun testOrDoubleAssignment() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = ("X" eq 1) or ("X" eq 2)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                sequenceOf(
                    query.yes("X" to 1),
                    query.yes("X" to 2)
                )
            )
            expected.assertingEquals(solutions)
        }
    }
}
