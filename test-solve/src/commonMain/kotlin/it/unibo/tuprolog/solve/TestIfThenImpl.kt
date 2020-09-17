package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog

internal class TestIfThenImpl(private val solverFactory: SolverFactory) : TestIfThen {
    override fun testIfThenTrue() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "->"(true, true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }

    override fun testIfThenFail() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "->"(true, fail)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testIfThenFailTrue() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "->"(fail, true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testIfThenXtoOne() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "->"(true, "X" `=` 1)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to 1)),
                solutions
            )
        }
    }

    override fun testIfThenXOr() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "->"(("X" `=` 1) or ("X" `=` 2), true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to 1)),
                solutions
            )
        }
    }

    override fun testIfThenOrWithDoubleSub() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "->"(true, ("X" `=` 1) or ("X" `=` 2))
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
}
