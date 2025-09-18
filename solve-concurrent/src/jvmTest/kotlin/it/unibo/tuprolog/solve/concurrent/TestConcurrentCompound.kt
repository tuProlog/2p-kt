package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentCompound<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testCompoundDec() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = compound(33.3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testCompoundNegDec() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = compound(-33.3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testCompoundNegA() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = compound("-"("a"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testCompoundAny() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = compound(`_`)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testCompoundA() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = compound("a")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testCompoundAOfB() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = compound("a"("b"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testCompoundListA() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = compound(logicListOf("a"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }
}
