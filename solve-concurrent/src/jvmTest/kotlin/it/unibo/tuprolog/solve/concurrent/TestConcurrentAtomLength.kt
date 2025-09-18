package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentAtomLength<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testAtomLengthNoVar() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = atom_length("test", 4)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomLengthSecondIsVar() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = atom_length("test", X)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 4))

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomLengthFirstIsVar() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = char_code("X", "a")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected =
                fromSequence(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("char_code", 2),
                            TypeError.Expected.INTEGER,
                            atomOf("a"),
                            index = 1,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomLengthSecondIsVar2() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = atom_length("testLength", X)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 10))

            expected.assertingEquals(solutions)
        }
    }

    fun testAtomLengthFail() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = atom_length("test", 5)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }
}
