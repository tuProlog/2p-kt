package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming

internal class TestIfThenImpl(
    private val solverFactory: SolverFactory,
) : TestIfThen {
    override fun testIfThenTrue() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "->"(true, true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testIfThenFail() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "->"(true, fail)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testIfThenFailTrue() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "->"(fail, true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testIfThenXtoOne() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "->"(true, "X" eq 1)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to 1)),
                solutions,
            )
        }
    }

    override fun testIfThenXOr() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "->"(("X" eq 1) or ("X" eq 2), true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to 1)),
                solutions,
            )
        }
    }

    override fun testIfThenOrWithDoubleSub() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "->"(true, ("X" eq 1) or ("X" eq 2))
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
}
