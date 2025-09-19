package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentInteger<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testIntPositiveNum() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = integer(3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testIntNegativeNum() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = integer(intOf(-3))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testIntDecNum() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = integer(3.3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testIntX() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = integer("X")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testIntAtom() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = integer("atom")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }
}
