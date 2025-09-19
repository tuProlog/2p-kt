package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming

internal class TestIfThenElseImpl(
    private val solverFactory: SolverFactory,
) : TestIfThenElse {
    override fun testIfTrueElseFail() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ";"("->"(true, true), fail)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testIfFailElseTrue() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ";"("->"(fail, true), true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testIfTrueThenElseFail() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ";"("->"(true, fail), fail)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testIfFailElseFail() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ";"("->"(fail, true), fail)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testIfXTrueElseX() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ";"("->"(true, ("X" eq 1)), "X" eq 2)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to 1)),
                solutions,
            )
        }
    }

    override fun testIfFailElseX() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ";"("->"(fail, ("X" eq 1)), "X" eq 2)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to 2)),
                solutions,
            )
        }
    }

    override fun testIfThenElseOrWithDoubleSub() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ";"("->"(true, ("X" eq 1) or ("X" eq 2)), true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                with(query) {
                    kotlin.collections.listOf(
                        yes("X" to 1),
                        yes("X" to 2),
                    )
                },
                solutions,
            )
        }
    }

    override fun testIfOrElseTrue() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ";"("->"(("X" eq 1) or ("X" eq 2), true), true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to 1)),
                solutions,
            )
        }
    }
}
