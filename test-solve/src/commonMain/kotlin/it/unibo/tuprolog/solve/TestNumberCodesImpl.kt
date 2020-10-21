package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.error.TypeError

class TestNumberCodesImpl(private val solverFactory: SolverFactory) : TestNumberCodes {
    override fun testNumberCodesListIsVar() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number_codes(33, "L")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("L" to listOf(51, 51))),
                solutions
            )
        }
    }

    override fun testNumberCodesNumIsDecimal() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number_codes(33.1, "L")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("L" to listOf(51, 51, 46, 49))),
                solutions
            )
        }
    }

    override fun testNumberCodesListIsVar2() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number_codes(9921.1, "L")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("L" to listOf(57, 57, 50, 49, 46, 49))),
                solutions
            )
        }
    }

    override fun testNumberCodesOk() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number_codes(33, listOf(51, 51))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }

    override fun testNumberCodesCompleteTest() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number_codes(34, listOf(51, 52))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }

    override fun testNumberCodesNegativeNumber() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number_codes("X", listOf(45, 51, 46, 56))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to -3.8)),
                solutions
            )
        }
    }

    override fun testNumberCodesChar() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number_codes("a", "L")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("number_codes", 2),
                            TypeError.Expected.NUMBER,
                            atomOf("a"),
                            index = 0
                        )
                    )
                ),
                solutions
            )
        }
    }
}
