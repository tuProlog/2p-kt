package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog

internal class TestCompoundImpl(private val solverFactory: SolverFactory) : TestCompound {
    override fun testCompoundDec() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = compound(33.3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testCompoundNegDec() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = compound(-33.3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testCompoundNegA() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = compound("-"("a"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }

    override fun testCompoundAny() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = compound(`_`)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testCompoundA() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = compound("a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testCompoundAOfB() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = compound("a"("b"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }

    override fun testCompoundListA() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = compound(listOf("a"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }
}
