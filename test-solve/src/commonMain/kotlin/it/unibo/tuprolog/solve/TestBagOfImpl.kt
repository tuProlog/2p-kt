package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

class TestBagOfImpl(private val solverFactory: SolverFactory) : TestBagOf {

    override fun testBagXInDifferentValues() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = bagof("X", ("X" `=` 1) or ("X" `=` 2), "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("S" to listOf(1, 2))),
                solutions
            )
        }
    }

    override fun testBagOfFindX() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = bagof("X", ("X" `=` 1) or ("X" `=` 2), "X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("X" to listOf(1, 2))),
                solutions
            )
        }
    }

    override fun testBagOfYXZ() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = bagof("X", ("X" `=` "Y") or ("X" `=` "Z"), "L")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("L" to listOf("Y", "Z"))),
                solutions
            )
        }
    }

    override fun testBagOfFail() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = bagof("X", fail, "L")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )
        }
    }

    override fun testBagOfSameAsFindall() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = bagof("X", "Y" `^` (("X" `=` 1) or ("Y" `=` 2)), "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("S" to listOf(1))),
                solutions
            )
        }
    }

    override fun testBagOfInstanceError() {
        prolog {
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
                            index = 1
                        )
                    )
                ),
                solutions
            )
        }
    }

    override fun testBagOfTypeError() {
        prolog {
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
                            index = 1
                        )
                    )
                ),
                solutions
            )
        }
    }
}
