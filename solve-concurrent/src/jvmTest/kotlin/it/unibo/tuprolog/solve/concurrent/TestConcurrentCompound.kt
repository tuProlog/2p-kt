package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentCompound<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    fun testCompoundDec() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = compound(33.3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testCompoundNegDec() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = compound(-33.3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testCompoundNegA() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = compound("-"("a"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testCompoundAny() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = compound(`_`)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testCompoundA() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = compound("a")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testCompoundAOfB() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = compound("a"("b"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testCompoundListA() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = compound(listOf("a"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }
}
