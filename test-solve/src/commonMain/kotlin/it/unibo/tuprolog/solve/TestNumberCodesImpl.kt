package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.TypeError

class TestNumberCodesImpl(
    private val solverFactory: SolverFactory,
) : TestNumberCodes {
    override fun testNumberCodesListIsVar() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number_codes(33, "L")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("L" to logicListOf(51, 51))),
                solutions,
            )
        }
    }

    override fun testNumberCodesNumIsDecimal() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number_codes(33.1, "L")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("L" to logicListOf(51, 51, 46, 49))),
                solutions,
            )
        }
    }

    override fun testNumberCodesListIsVar2() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number_codes(9921.1, "L")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("L" to logicListOf(57, 57, 50, 49, 46, 49))),
                solutions,
            )
        }
    }

    override fun testNumberCodesOk() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number_codes(33, logicListOf(51, 51))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testNumberCodesCompleteTest() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number_codes(34, logicListOf(51, 52))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testNumberCodesNegativeNumber() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number_codes("X", logicListOf(45, 51, 46, 56))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("X" to numOf("-3.8"))),
                solutions,
            )
        }
    }

    override fun testNumberCodesChar() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number_codes("a", "L")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("number_codes", 2),
                            TypeError.Expected.NUMBER,
                            atomOf("a"),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }
}
