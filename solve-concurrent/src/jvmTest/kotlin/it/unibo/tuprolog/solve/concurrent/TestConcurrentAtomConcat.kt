package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentAtomConcat<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testAtomConcatThirdIsVar() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = atom_concat("test", "concat", "X")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to atomOf("testconcat")))

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomConcatFails() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = atom_concat("test", "concat", "test")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomConcatSecondIsVar() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = atom_concat("test", "X", "testTest")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to atomOf("Test")))

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomConcatFirstIsVar() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = atom_concat("X", "query", "testquery")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to atomOf("test")))

            expected.assertingEquals(solutions)
        }
    }
}
