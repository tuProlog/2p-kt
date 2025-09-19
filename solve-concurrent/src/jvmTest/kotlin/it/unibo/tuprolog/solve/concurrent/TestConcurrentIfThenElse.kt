package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentIfThenElse<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testIfTrueElseFail() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = ";"("->"(true, true), fail)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testIfFailElseTrue() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = ";"("->"(fail, true), true)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testIfTrueThenElseFail() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = ";"("->"(true, fail), fail)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testIfFailElseFail() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = ";"("->"(fail, true), fail)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testIfXTrueElseX() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = ";"("->"(true, ("X" eq 1)), "X" eq 2)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 1))

            expected.assertingEquals(solutions)
        }
    }

    fun testIfFailElseX() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = ";"("->"(fail, ("X" eq 1)), "X" eq 2)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 2))

            expected.assertingEquals(solutions)
        }
    }

    fun testIfThenElseOrWithDoubleSub() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = ";"("->"(true, ("X" eq 1) or ("X" eq 2)), true)
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

    fun testIfOrElseTrue() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = ";"("->"(("X" eq 1) or ("X" eq 2), true), true)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 1))

            expected.assertingEquals(solutions)
        }
    }
}
