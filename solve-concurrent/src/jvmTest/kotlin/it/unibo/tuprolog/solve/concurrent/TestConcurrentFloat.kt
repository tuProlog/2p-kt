package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentFloat<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    fun testFloatDec() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = float(3.3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testFloatDecNeg() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = float(-3.3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testFloatNat() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = float(3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testFloatAtom() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = float("atom")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testFloatX() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = float("X")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }
}
