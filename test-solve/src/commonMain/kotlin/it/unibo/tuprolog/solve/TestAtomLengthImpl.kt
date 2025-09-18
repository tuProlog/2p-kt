package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.TypeError

class TestAtomLengthImpl(
    private val solverFactory: SolverFactory,
) : TestAtomLength {
    override fun testAtomLengthNoVar() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_length("test", 4)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testAtomLengthSecondIsVar() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_length("test", X)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to 4)),
                solutions,
            )
        }
    }

    override fun testAtomLengthFirstIsVar() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = char_code("X", "a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("char_code", 2),
                            TypeError.Expected.INTEGER,
                            atomOf("a"),
                            index = 1,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testAtomLengthSecondIsVar2() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_length("testLength", X)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to 10)),
                solutions,
            )
        }
    }

    override fun testAtomLengthFail() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_length("test", 5)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )
        }
    }
}
