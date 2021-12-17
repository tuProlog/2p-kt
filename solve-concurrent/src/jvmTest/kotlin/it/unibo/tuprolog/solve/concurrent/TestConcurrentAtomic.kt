package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentAtomic<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    fun testAtomicAtom() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = atomic("atom")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomicAofB() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = atomic("a"("b"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomicVar() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = atomic("Var")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomicEmptyList() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = atomic(emptyList)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomicNum() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = atomic(6)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomicNumDec() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = atomic(3.3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }
}
