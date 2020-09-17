package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog

internal class TestIfThenElseImpl(private val solverFactory: SolverFactory) : TestIfThenElse {
    override fun testIfTrueElseFail() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ";"("->"(true, true), fail)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }

    override fun testIfFailElseTrue() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ";"("->"(fail, true), true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }

    override fun testIfTrueThenElseFail() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ";"("->"(true, fail), fail)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testIfFailElseFail() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ";"("->"(fail, true), fail)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testIfXTrueElseX() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ";"("->"(true, ("X" `=` 1)), "X" `=` 2)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to 1)),
                solutions
            )
        }
    }

    override fun testIfFailElseX() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ";"("->"(fail, ("X" `=` 1)), "X" `=` 2)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to 2)),
                solutions
            )
        }
    }

    override fun testIfThenElseOrWithDoubleSub() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ";"("->"(true, ("X" `=` 1) or ("X" `=` 2)), true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                with(query) {
                    kotlin.collections.listOf(
                        yes("X" to 1),
                        yes("X" to 2)
                    )
                },
                solutions
            )
        }
    }

    override fun testIfOrElseTrue() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ";"("->"(("X" `=` 1) or ("X" `=` 2), true), true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to 1)),
                solutions
            )
        }
    }
}
