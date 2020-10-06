package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog

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

            val query = number_chars(33, "L")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("L" to listOf(51, 51))),
                solutions
            )
        }
    }

    override fun testNumberCodesOk() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number_codes(33, listOf("0'3", "0'3"))
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

            val query = number_codes(33.0, listOf("0'3", "0'.", "0'3", "0'E", "0'+", "0'0", "0'1"))
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

            val query = number_codes("X", listOf("-", "0'2", "0'5"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to -25)),
                solutions
            )
        }
    }

    override fun testNumberCodesChar() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = number_codes("X", listOf("0'0", "39", "0'a"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to 97)),
                solutions
            )
        }
    }
}
