package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

class TestBagOfImpl(private val solverFactory: SolverFactory) : TestBagOf {
    override fun testBagXInDifferentValues() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = bagof("X", ("X" eq 1) or ("X" eq 2), "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("S" to listOf(1, 2))),
                solutions,
            )
        }
    }

    override fun testBagOfFindX() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = bagof("X", ("X" eq 1) or ("X" eq 2), "X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("X" to listOf(1, 2))),
                solutions,
            )
        }
    }

    override fun testBagOfYXZ() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = bagof("X", ("X" eq "Y") or ("X" eq "Z"), "L")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("L" to listOf("Y", "Z"))),
                solutions,
            )
        }
    }

    override fun testBagOfFail() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = bagof("X", fail, "L")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions,
            )
        }
    }

    override fun testBagOfSameAsFindall() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = bagof("X", "Y" sup (("X" eq 1) or ("Y" eq 2)), "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("S" to listOf(1))),
                solutions,
            )
        }
    }

    override fun testBagOfInstanceError() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = bagof("X", "G", "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("bagof", 3),
                            varOf("G"),
                            index = 1,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testBagOfTypeError() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = bagof("X", 1, "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("bagof", 3),
                            TypeError.Expected.CALLABLE,
                            numOf(1),
                            index = 1,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }
}
