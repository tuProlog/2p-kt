package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentAtomic<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testAtomicAtom() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = atomic("atom")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomicAofB() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = atomic("a"("b"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomicVar() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = atomic("Var")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomicEmptyList() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = atomic(emptyLogicList)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomicNum() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = atomic(6)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomicNumDec() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = atomic(3.3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }
}
