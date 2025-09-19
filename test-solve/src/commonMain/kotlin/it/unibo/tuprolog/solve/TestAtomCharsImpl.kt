package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

class TestAtomCharsImpl(
    private val solverFactory: SolverFactory,
) : TestAtomChars {
    override fun atomCharsTestFirstIsVar() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_chars("X", logicListOf("t", "e", "s", "t"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("X" to "test")),
                solutions,
            )
        }
    }

    override fun atomCharsTestYes() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_chars("test", logicListOf("t", "e", "s", "t"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun atomCharsTestOneCharIsVar() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_chars("test", logicListOf("t", "e", "s", "T"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes(("T" to "t"))),
                solutions,
            )
        }
    }

    override fun atomCharsTestFailure() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_chars("test1", logicListOf("t", "e", "s", "T"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun atomCharsTestEmpty() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_chars("", "L")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("L" to emptyLogicList)),
                solutions,
            )
        }
    }

    override fun atomCharsTestListHead() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_chars("ac", logicListOf("a", "C"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("C" to "c")),
                solutions,
            )
        }
    }

    override fun atomCharsTestIstantationErrorCheck() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_chars("A", "L")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("atom_chars", 2),
                            varOf("A"),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun atomCharsTestTypeErrorCheck() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_chars("A", "iso")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("atom_chars", 2),
                            TypeError.Expected.LIST,
                            atomOf("iso"),
                            index = 1,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }
}
