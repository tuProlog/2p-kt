package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentAtom<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testAtomAtom() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = atom("atom")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomString() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = atom("string")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomAofB() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = atom("a"("b"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomVar() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = atom("Var")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomEmptyList() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = atom(emptyLogicList)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomNum() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = atom(6)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomNumDec() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = atom(3.3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }
}
