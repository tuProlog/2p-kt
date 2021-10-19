package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentIfThen<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    fun testIfThenTrue() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = "->"(true, true)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testIfThenFail() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = "->"(true, fail)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testIfThenFailTrue() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = "->"(fail, true)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testIfThenXtoOne() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = "->"(true, "X" `=` 1)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 1))

            expected.assertingEquals(solutions)
        }
    }

    fun testIfThenXOr() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = "->"(("X" `=` 1) or ("X" `=` 2), true)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 1))

            expected.assertingEquals(solutions)
        }
    }

    fun testIfThenOrWithDoubleSub() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = "->"(true, ("X" `=` 1) or ("X" `=` 2))
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
