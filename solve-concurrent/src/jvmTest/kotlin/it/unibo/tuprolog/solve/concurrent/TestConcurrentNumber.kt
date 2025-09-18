package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentNumber<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testBasicNum() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = number(3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testDecNum() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = number(3.3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testNegNum() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = number(-3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testLetterNum() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = number("a")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testXNum() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = number("X")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }
}
