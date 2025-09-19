package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentIfThen<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testIfThenTrue() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = "->"(true, true)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testIfThenFail() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = "->"(true, fail)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testIfThenFailTrue() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = "->"(fail, true)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testIfThenXtoOne() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = "->"(true, "X" eq 1)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 1))

            expected.assertingEquals(solutions)
        }
    }

    fun testIfThenXOr() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = "->"(("X" eq 1) or ("X" eq 2), true)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 1))

            expected.assertingEquals(solutions)
        }
    }

    fun testIfThenOrWithDoubleSub() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = "->"(true, ("X" eq 1) or ("X" eq 2))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected =
                fromSequence(
                    sequenceOf(
                        query.yes("X" to 1),
                        query.yes("X" to 2),
                    ),
                )

            expected.assertingEquals(solutions)
        }
    }
}
